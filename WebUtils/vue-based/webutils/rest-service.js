import {$logger, $utils, $appConfiguration} from "./common.js";

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

	"invokePost": function(url, body, settings)
	{
		settings = (settings == null) ? {} : settings;
		settings.method = "POST";

		body = (body != null) ? JSON.stringify(body) : null;
		
		this.invokeRestApi(url, body, settings);
	},

	"invokePut": function(url, body, settings)
	{
		settings = (settings == null) ? {} : settings;
		settings.method = "PUT";

		body = (body != null) ? JSON.stringify(body) : null;
		
		this.invokeRestApi(url, body, settings);
	},

	"invokeGet": function(url, params, settings)
	{
		settings = (settings == null) ? {} : settings;
		settings.method = "GET";

		this.invokeRestApi(url, params, settings);
	},

	"invokeDelete": function(url, params, settings)
	{
		settings = (settings == null) ? {} : settings;
		settings.method = "DELETE";

		this.invokeRestApi(url, params, settings);
	},
	
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