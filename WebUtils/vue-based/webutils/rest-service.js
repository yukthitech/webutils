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
		
	/**
	 * Generic method for invoking REST APIs. This is an internal method and should not be called directly.
	 * All `invoke` methods accept a `settings` object to configure the request.
	 *
	 * @param {string} url - The API endpoint.
	 * @param {object} data - The request payload.
	 * @param {object} settings - Configuration for the request.
	 * @param {object} [settings.context=null] - The `this` context for the callback functions.
	 * @param {Function} [settings.onSuccess=null] - Callback function executed on a successful (2xx) response. Receives a `result` object.
	 * @param {Function} [settings.onError=null] - Callback function executed on an error (non-2xx) response. Receives an `error` object.
	 * @param {Function} [settings.onResult=null] - Callback function executed after `onSuccess` or `onError`.
	 * @param {string} [settings.contentType="application/json"] - The content type of the request.
	 * @param {boolean} [settings.async=true] - Whether the request should be asynchronous.
	 * @param {boolean} [settings.multipart=false] - Set to `true` for multipart/form-data requests (e.g., file uploads).
	 * @param {boolean} [settings.includeAuthToken=true] - If `false`, the `AUTH_TOKEN` header will not be sent with the request.
	 */
	"invokeRestApi": function(url, data, settings)
	{
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
			settings.headers["AUTH_TOKEN"] = sessionStorage.getItem("authToken");
		}

		$.ajax({
			"url": url,
			"method": settings.method,
			"dataType": 'json',
			"contentType": settings.contentType,
			"data": data,
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
					$logger.error("Got error as - {}", resData);
				}catch(ex)
				{
					$logger.error("Failed to parsed error response text as json. Error - {}", ex);
					$logger.error("Error Response text: " + jqXHR.responseText);
					
					resData = {"code": jqXHR.statusCode().status, "message": "Server Error"};
				}
				
				var resultObj = {
					"response": resData,
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

		body = (body != null) ? JSON.stringify(body) : null;
		
		this.invokeRestApi(url, body, settings);
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

		body = (body != null) ? JSON.stringify(body) : null;
		
		this.invokeRestApi(url, body, settings);
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

		this.invokeRestApi(url, params, settings);
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

		this.invokeRestApi(url, params, settings);
	},
	
	/**
     * Fetches a model definition from the server and caches it.
     * @param {string} name - The name of the model.
     * @param {Function} successCallback - A function to call with the fetched model definition.
     * @param {boolean} [isNoAuthReq=false] - If `true`, the request is made to a non-authenticated endpoint.
     */
	"fetchModelDef": function(name, successCallback, isNoAuthReq)
	{
		if(this.modelDefCache[name])
		{
			successCallback(this.modelDefCache[name]);
			return;
		}

		let callback = $.proxy(function(result) {
			this.$this.modelDefCache[this.name] = result.response.modelDef;
			this.successCallback(result.response.modelDef)
		}, {"$this": this, "successCallback": successCallback, "name": name});

		let urlPrefix = isNoAuthReq ? "/api/models/noAuth/fetch/" : "/api/models/fetch/";
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
		
		if(!parentValue)
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
			
			for(var lov of result.response.lovList)
			{
				lovOptions.push(lov);
			}
			
			this.$this.lovCache[this.cacheKey] = lovOptions;
			this.successCallback(lovOptions)
		}, {"$this": this, "successCallback": successCallback, "name": name, "cacheKey": cacheKey});
		
		var urlPrefix = isNoAuthReq ? "/api/lov/noAuth/fetch/" : "/api/lov/fetch/";
		var url = urlPrefix + name + "/" + lovType;
		
		if(parentValue)
		{
			urlPrefix = isNoAuthReq ? "/api/lov/noAuth/fetchDependentLov/" : "/api/lov/fetchDependentLov/";
			url = urlPrefix + name + "/" + lovType + "/" + parentValue;
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
	}
};
