$.restService = {
	"lovCache": {},
	
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
				this.onSuccess({
					"response": resData,
					"statusCode": jqXHR.statusCode().status,
					"responseCode": resData.code
				});
			}, settings),
			
			"error": $.proxy(function(jqXHR, textStatus, errorThrown){
				var resData = null;
				
				//on session expiry, if app is configure to handle expiry
				// invoke the same and return
				if(jqXHR.statusCode().status == 401 && $.appConfiguration.onSessionExpiry)
				{
					$.logger.debug("Invoking session expiry handler of app-config");
					$.appConfiguration.onSessionExpiry();
					return;
				}
				
				try
				{
					resData = $.parseJSON(jqXHR.responseText);
					$.logger.error("Got error as - {}", resData);
				}catch(ex)
				{
					$.logger.error("Failed to parsed error response text as json. Error - {}", ex);
					$.logger.error("Error Response text: " + jqXHR.responseText);
				}
				
				this.onError({
					"response": resData,
					"statusCode": jqXHR.statusCode().status,
					"responseCode": -1
				});
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
	
	"fetchLovValues": function(name, lovType, successCallback)
	{
		if(this.lovCache[name])
		{
			successCallback(this.lovCache[name]);
			return;
		}
		
		var callback = $.proxy(function(result) {
			var lovOptions = [];
			
			for(var lov of result.response.lovList)
			{
				lovOptions.push(lov);
			}
			
			this.$this.lovCache[this.name] = lovOptions;
			this.successCallback(lovOptions)
		}, {"$this": this, "successCallback": successCallback, "name": name});
		
		$.restService.invokeGet(
				"/api/lov/fetch/" + name + "/" + lovType, 
				null,
				{
					"context": this, 
					"onSuccess": callback
				}
			);
	}
};