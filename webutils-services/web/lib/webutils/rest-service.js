import {$logger, $utils, $appConfiguration} from "./common.js";

/**
 * Handles all REST API interactions with the backend.
 * @namespace $restService
 */
export var $restService = {
	"lovCache": {},
	"modelDefCache": {},
	
	"dummy": function()
	{
		
	},

	"buildNestedErrorObject": function(path, message) {
		if(!path) {
			return message;
		}

		let rootObj = {};
		let curObj = rootObj;
		let pathParts = path.split(".");

		for(let i = 0; i < pathParts.length; i++)
		{
			let part = pathParts[i];
			let isLast = (i == pathParts.length - 1);

			if(isLast)
			{
				curObj[part] = message;
				break;
			}

			if(!curObj[part] || typeof(curObj[part]) != "object")
			{
				curObj[part] = {};
			}

			curObj = curObj[part];
		}

		return rootObj;
	},

	"mergeErrorObjects": function(target, source) {
		if(!source)
		{
			return target;
		}

		if(!target)
		{
			target = {};
		}

		for(let prop in source)
		{
			let srcVal = source[prop];

			if(typeof(srcVal) == "string")
			{
				target[prop] = srcVal;
			}
			else
			{
				if(!target[prop])
				{
					target[prop] = {};
				}
				
				this.mergeErrorObjects(target[prop], srcVal);
			}
		}

		return target;
	},

	"normalizeIndexedErrors": function(resData) {
		if(!resData || !resData.errors || typeof(resData.errors) != "object")
		{
			return;
		}

		let originalErrors = resData.errors;
		let normalizedErrors = {};
		let indexedErrorsMap = {};
		let hasIndexedErrors = false;
		let expr = /^([^\[\].]+)\[(\d+)\](?:\.(.+))?$/;

		for(let fieldPath in originalErrors)
		{
			let errMsg = originalErrors[fieldPath];
			let match = fieldPath.match(expr);

			if(!match)
			{
				normalizedErrors[fieldPath] = errMsg;
				continue;
			}

			hasIndexedErrors = true;

			let fieldName = match[1];
			let index = parseInt(match[2], 10);
			let nestedPath = match[3];

			if(!indexedErrorsMap[fieldName])
			{
				indexedErrorsMap[fieldName] = {};
			}

			if(!indexedErrorsMap[fieldName][index])
			{
				indexedErrorsMap[fieldName][index] = {"index": index, "error": null};
			}

			let entry = indexedErrorsMap[fieldName][index];

			// For nested row model errors (e.g., field[1].name), build nested object.
			if(nestedPath)
			{
				let nestedErrObj = this.buildNestedErrorObject(nestedPath, errMsg);

				if(entry.error == null)
				{
					entry.error = {};
				}

				entry.error = this.mergeErrorObjects(entry.error, nestedErrObj);
			}
			// For direct list value errors (e.g., skills[1]), keep a string message.
			else
			{
				entry.error = errMsg;
			}
		}

		if(!hasIndexedErrors)
		{
			return;
		}

		for(let fieldName in indexedErrorsMap)
		{
			let indexObj = indexedErrorsMap[fieldName];
			let sortedIndexKeys = Object.keys(indexObj).sort(function(a, b) {
				return parseInt(a, 10) - parseInt(b, 10);
			});

			normalizedErrors[fieldName] = [];

			for(let idxKey of sortedIndexKeys)
			{
				normalizedErrors[fieldName].push(indexObj[idxKey]);
			}
		}

		resData.errors = normalizedErrors;
	},
	
	"proxy": function(context, func)
	{
		if(!func)
		{
			return this.dummy;
		}
		
		if(!context)
		{
			context = {};
		}
		
		return $.proxy(func, context);
	},
	
	"extractFileObjects": function(body, fieldName, files) {
		let fieldValue = body[fieldName];
		
		if(!fieldValue) {
			return;
		}
		
		//if single file data is present
		if(!Array.isArray(fieldValue))
		{
			//if file is found, add to request as multi part and remove from entity
			files[files.length] = {"name" : fieldName, "file" : fieldValue};
			delete body[fieldName];
			return;
		}
		
		//if field is an file array
		for(var j = 0; j < fieldValue.length; j++)
		{
			files[files.length] = {"name" : fieldName, "file" : fieldValue[j]};
		}
		
		delete body[fieldName];
	},
	
	"buildModelRequest": function(body, settings) {
		// clone the body to avoid modifying the original object
		if(body) {
			body = {...body};
		}

		let modelDefName = settings["modelDef"];
		 
		if(!this.modelDefCache[modelDefName]) {
			throw "No model def found on cache with name: " + modelDefName;
		}
		
		let modelDef = this.modelDefCache[modelDefName];
		let files= [];
		let fileFieldsFound = false;
		
		for(let i = 0; i < modelDef.fields.length; i++)
		{
			if(modelDef.fields[i].fieldType == 'FILE') {
				fileFieldsFound = true;
				this.extractFileObjects(body, modelDef.fields[i].name, files);
				continue;
			}
			
			if(modelDef.fields[i].fieldType == 'IMAGE') {
				fileFieldsFound = true;
				this.extractFileObjects(body, modelDef.fields[i].name, files);
				continue;
			}
		}
		
		// if no file fields are present, return the simple data object
		if(!fileFieldsFound) {
			return JSON.stringify(body);
		}
		
		//convert entity into json and add it to request
		var request = new FormData();
		settings.multipart = true;
		
		let dataJson = JSON.stringify(body);
		request.append("default", 
			new Blob(
				[dataJson],
				{type: "application/json"}
			)
		);

		for(var i = 0; i < files.length; i++)
		{
			request.append(files[i].name, files[i].file);
		}

		for (let [key, value] of request.entries()) {
			console.log(key, value, value.size);
		}

		return request;
	},
		
	/**
	 * Generic method for invoking REST APIs. This is an internal method and should not be called directly.
	 * All `invoke` methods accept a `settings` object to configure the request.
	 *
	 * @param {string} url - The API endpoint.
	 * @param {object} body - The request payload.
	 * @param {object} params - Url parameters.
	 * @param {object} settings - Configuration for the request.
	 * @param {object} [settings.modelDef=null] - If speificed, the body will get transformed as per the model def structure. And into multipart if files are present.
	 * @param {object} [settings.context=null] - The `this` context for the callback functions.
	 * @param {Function} [settings.onSuccess=null] - Callback function executed on a successful (2xx) response. Receives a `result` object.
	 * @param {Function} [settings.onError=null] - Callback function executed on an error (non-2xx) response. Receives an `error` object.
	 * @param {Function} [settings.onResult=null] - Callback function executed after `onSuccess` or `onError`.
	 * @param {string} [settings.contentType="application/json"] - The content type of the request.
	 * @param {boolean} [settings.async=true] - Whether the request should be asynchronous.
	 * @param {boolean} [settings.multipart=false] - Set to `true` for multipart/form-data requests (e.g., file uploads).
	 * @param {boolean} [settings.includeAuthToken=true] - If `false`, the `AUTH_TOKEN` header will not be sent with the request.
	 */
	"invokeRestApi": function(url, body, params, settings)
	{
		// if modelDef is specified on settings
		if(settings.modelDef) {
			body = this.buildModelRequest(body, settings);
		}
		// if model transformation is not required and body is present
		//   convert body object to json string
		else if(body) {
			body = JSON.stringify(body);
		}
		
		// if both body and params are present, include params in url
		if(body && params) {
			let urlObj = new URL(url, window.location.origin); // Creates a URL object from the base URL and current origin.

			for (let key in params) {
			  urlObj.searchParams.append(key, params[key]); // Appends each key-value pair as a query parameter.
			}
			
			url = urlObj.toString();
		}
		// when only params is present, send params as body (data)
		else if(!body && params) {
			body = params;
		}
		
		settings.onSuccess = this.proxy(settings.context, settings.onSuccess);
		settings.onError = this.proxy(settings.context, settings.onError);
		
		settings.contentType = settings.contentType ? settings.contentType : "application/json";
		settings.async = (settings.async == false) ? false : true;
		
		//set headers as required for attachments
		if(settings.multipart)
		{
			settings.contentType = false;
			settings.processData = false;
		}
		
		settings.headers = {};
		
		if(settings.includeAuthToken != false)
		{
			var token = sessionStorage.getItem("authToken");
			if(token)
			{
				settings.headers["Authorization"] = "Bearer " + token;
			}
		}

		$.ajax({
			"url": url,
			"method": settings.method,
			"dataType": 'json',
			"contentType": settings.contentType,
			"data": body,
			"async": settings.async,
			"cache": false,
			"processData": settings.processData,
			"headers": settings.headers,
			
			"success": $.proxy(function(resData, textStatus, jqXHR){
				var resultObj = {
					"response": resData,
					"statusCode": jqXHR.statusCode().status,
					"responseCode": resData.code,
					"extraInfo": this.extraInfo
				};
				
				if(this.onSuccess)
				{ 
					this.onSuccess(resultObj);
				}
				
				if(this.onResult)
				{
					this.onResult(resultObj);
				}
					
			}, settings),
			
			"error": $.proxy(function(jqXHR, textStatus, errorThrown){
				var resData = null;
				
				//on session expiry, if app is configure to handle expiry
				// invoke the same and return
				if(jqXHR.statusCode().status == 401 && $appConfiguration.onSessionExpiry)
				{
					$logger.debug("Invoking session expiry handler of app-config");
					$appConfiguration.onSessionExpiry();
					return;
				}
				
				try
				{
					resData = $.parseJSON(jqXHR.responseText);
					$restService.normalizeIndexedErrors(resData);
					$logger.error("Got error as - {}", resData);
				}catch(ex)
				{
					$logger.error("Failed to parsed error response text as json. Error - {}", ex);
					$logger.error("Error Response text: " + jqXHR.responseText);
					
					resData = {"code": jqXHR.statusCode().status, "message": "Server Error"};
				}
				
				var resultObj = {
					"response": resData,
					"errors": resData ? resData.errors : null,
					"statusCode": jqXHR.statusCode().status,
					"responseCode": -1,
					"extraInfo": this.extraInfo
				};
				
				if(this.onError)
				{
					this.onError(resultObj);
				}
				
				if(this.onResult)
				{
					this.onResult(resultObj);
				}
			}, settings)
		});
	},

	/**
     * Sends a POST request.
     * @param {string} url - The API endpoint.
     * @param {object} body - The request payload, which will be JSON-stringified.
     * @param {object} settings - Configuration for the request. See invokeRestApi for details.
     */
	"invokePost": function(url, body, settings)
	{
		settings = (settings == null) ? {} : settings;
		settings.method = "POST";

		this.invokeRestApi(url, body, null, settings);
	},

	/**
     * Sends a PUT request.
     * @param {string} url - The API endpoint.
     * @param {object} body - The request payload, which will be JSON-stringified.
     * @param {object} settings - Configuration for the request. See invokeRestApi for details.
     */
	"invokePut": function(url, body, settings)
	{
		settings = (settings == null) ? {} : settings;
		settings.method = "PUT";

		this.invokeRestApi(url, body, null, settings);
	},

	/**
     * Sends a GET request.
     * @param {string} url - The API endpoint.
     * @param {object} params - An object of query parameters to be appended to the URL.
     * @param {object} settings - Configuration for the request. See invokeRestApi for details.
     */
	"invokeGet": function(url, params, settings)
	{
		settings = (settings == null) ? {} : settings;
		settings.method = "GET";

		this.invokeRestApi(url, null, params, settings);
	},

	/**
     * Sends a DELETE request.
     * @param {string} url - The API endpoint.
     * @param {object} params - An object of query parameters to be appended to the URL.
     * @param {object} settings - Configuration for the request. See invokeRestApi for details.
     */
	"invokeDelete": function(url, params, settings)
	{
		settings = (settings == null) ? {} : settings;
		settings.method = "DELETE";

		this.invokeRestApi(url, null, params, settings);
	},
	
	/**
     * Fetches a model definition from the server and caches it.
     * @param {string} name - The name of the model.
     * @param {Function} successCallback - A function to call with the fetched model definition.
     */
	"fetchModelDef": function(name, successCallback)
	{
		if(this.modelDefCache[name])
		{
			let cloned = $utils.deepClone(this.modelDefCache[name]);
			successCallback(cloned);
			return;
		}

		let callback = $.proxy(function(result) {
			this.$this.modelDefCache[this.name] = result.response.value;
			this.successCallback(result.response.value)
		}, {"$this": this, "successCallback": successCallback, "name": name});

		let urlPrefix = "/api/model/fetch/";
		let url = urlPrefix + name;
			
		$utils.executeWithInProgress($.proxy(function() {
			this.$this.invokeGet(
					this.url, 
					null,
					{
						"context": this.$this, 
						"onSuccess": this.callback
					}
				);
		}, {"url": url, "$this": this, "callback": callback}));
	},
	
	/**
     * Fetches a List of Values (LOV) from the server and caches it.
     * @param {string} name - The name of the LOV.
     * @param {string} lovType - The type of LOV.
     * @param {Function} successCallback - A function to call with the fetched LOV list.
     * @param {*} [parentValue] - An optional parent value for dependent LOVs.
     * @param {boolean} [isNoAuthReq=false] - If `true`, the request is made to a non-authenticated endpoint.
     */
	"fetchLovValues": function(name, lovType, successCallback, parentValue, isNoAuthReq)
	{
		var cacheKey = name;
		
		if(parentValue)
		{
			cacheKey += "-" + parentValue;
		}
		
		if(this.lovCache[cacheKey])
		{
			successCallback(this.lovCache[cacheKey]);
			return;
		}
		
		var callback = $.proxy(function(result) {
			var lovOptions = [];
			
			for(var lov of result.response.values)
			{
				lovOptions.push(lov);
			}
			
			this.$this.lovCache[this.cacheKey] = lovOptions;
			this.successCallback(lovOptions)
		}, {"$this": this, "successCallback": successCallback, "name": name, "cacheKey": cacheKey});
		
		var urlPrefix = isNoAuthReq ? "/api/lov/noAuth/fetch/" : "/api/lov/fetch/";
		var url = urlPrefix  + lovType + "/" + name;
		
		if(parentValue)
		{
			urlPrefix = isNoAuthReq ? "/api/lov/noAuth/fetchDependentLov/" : "/api/lov/fetchDependentLov/";
			url = urlPrefix + name + "/" + lovType + "/" + encodeURIComponent(parentValue);
		}
			
		$utils.executeWithInProgress($.proxy(function() {
			this.$this.invokeGet(
					this.url, 
					null,
					{
						"context": this.$this, 
						"onSuccess": this.callback
					}
				);
		}, {"url": url, "$this": this, "callback": callback}))
	},
	
	async fetchHtml(htmlUri) {
		const response = await fetch(htmlUri, { method: 'GET' });
		if (!response.ok) {
			throw new Error('Failed to fetch HTML content: ' + response.status);
		}
		return await response.text();
	}	
};
