var LOGIN_URI = "/auth/login";
var ACTIONS_URI = "/actions/fetch";
var URL_PARAM_PATTERN = /\{(\w+)\}/g;

$.application.factory('utils', [function(){
	var utils = {
		"ARG_PATERN" : /\{\}/g,
		"format": function(message, args, argIdx) {
			
			var idx = (argIdx == null || argIdx == undefined) ? 1 : argIdx;
			var finalMsg = null;
			
			if(typeof message == "string")
			{
				finalMsg = message.replace(this.ARG_PATERN, function(match, p1){
					var argVal = (idx < args.length) ? args[idx] : null;
					
					if(argVal && (typeof argVal == "object"))
					{
						argVal = JSON.stringify(argVal);
					}
					
					idx++;
					return argVal ? argVal : "null";
				});
			}
			else if(message && (typeof message == "object"))
			{
				finalMsg = JSON.stringify(message);
			}
			else
			{
				finalMsg = message;
			}

			return finalMsg;
		},
		
		"alert" : function(message, callback) {
			message = $.isArray(message) ? this.format(message[0], message, 1) : message;
			alert(message);
			
			if(callback)
			{
				callback();
			}
		},
		
		"info" : function(message) {
			message = $.isArray(message) ? this.format(message[0], message, 1) : message;
			alert(message);
		},
		
		"confirm" : function(message, callback) {
			message = $.isArray(message) ? this.format(message[0], message, 1) : message;
			var result = confirm(message);
			
			if(callback)
			{
				callback(result);
			}
		}
	};

	return utils;
}]);

$.application.factory('logger', ["utils", function(utils){
	var logger = {

		"debugEnabled" : ($.appConfiguration.debugEnabled ? true : false),
		"errorEnabled" : ($.appConfiguration.errorEnabled ? true : false),
		"traceEnabled" : ($.appConfiguration.traceEnabled ? true : false),

		"log": function(prefix, message, args) {
			var finalMsg = utils.format(message, args);

			console.log(prefix + " - " + finalMsg);
		},
		
		"debug" : function(message) {
			
			if(!this.debugEnabled)
			{
				return;
			}
			
			this.log("DEBUG", message, arguments);
		},

		"error" : function(message) {
			
			if(!this.errorEnabled)
			{
				return;
			}
			
			this.log("ERROR", message, arguments);
		},
		
		"trace" : function(message) {
			
			if(!this.traceEnabled)
			{
				return;
			}
			
			this.log("TRACE", message, arguments);
		}
	};
	
	return logger;
}]);

/**
 * This service provides context behaviour for APIs. Maintains the context
 * across the api calls on client side.
 */
$.application.factory('clientContext', ['logger', function(logger) {
	var clientContext = {
		/**
		 * Maintains the auth token that needs to be sent to server
		 * during every api request 
		 */
		"authToken" : null,
		
		/**
		 * Maintains the actions. Name as the key and action model as value
		 */
		"actionsMap" : null,
		
		/**
		 * Maintains the list of scripts added dynamically via client context
		 */
		scriptsAdded: [],
		
		/**
		 * Checks and return true if the current context is initialize, that
		 * is current is authenticated
		 */
		"isInitialized" : function() {
			return (this.authToken != null);
		},
		
		"invokeApi" : function(url, data, config) {
			var methodType = (config && config.methodType)? config.methodType: "POST";
			var contentType = (config && config.contentType)? config.contentType : 'application/x-www-form-urlencoded; charset=UTF-8';
			var processData = undefined;
			
			//set headers as required for attachments
			if(config.multipart)
			{
				contentType = false;
				processData = false;
			}
			
			var headers = {};
			
			if(this.authToken)
			{
				headers["AUTH_TOKEN"] = this.authToken;
			}
			
			var result = {
				"data" : null,
				status : null,
				error: null,
				backgroundApi : (config && config.backgroundApi),
				newAuthToken : null
			};
			
			$.ajax({
				  type: methodType,
				  dataType: 'json',
				  url: url,
				  data: data,
				  async: false,
				  cache: false,
				  "contentType": contentType,
				  "processData": processData,
				  "headers": headers,
				  
				  success: $.proxy(function(resData, textStatus, jqXHR){
					  this.data = resData;
					  this.status = jqXHR.statusCode().status;
					  
					  //if this is a normal api (not part of background api), then only
					  	//save updated auth header
					  if(!this.backgroundApi)
					  {
						  this.newAuthToken = jqXHR.getResponseHeader("AUTH_TOKEN");
					  }
					  
				  }, result),
				  
				  error: $.proxy(function(jqXHR, textStatus, errorThrown){
					  var resData = null;
					  
					  try
					  {
						  resData = $.parseJSON(jqXHR.responseText);
						  logger.error("Got error as - {}", resData);
					  }catch(ex)
					  {
						  logger.error("Failed to parsed error response text.- {}", ex);
						  logger.error("Error Response text: " + jqXHR.responseText);
					  }
					
					  this.data = resData;
					  this.status = jqXHR.statusCode().status;
					  this.error = errorThrown;
				  }, result)
			});
			
			if(result.status)
			{
				if(result.newAuthToken && result.newAuthToken != this.authToken)
				{
					logger.debug("New auth token recieved");
					this.authToken = result.newAuthToken;
					localStorage.setItem("authToken", this.authToken);
				}
				
				if(result.status >= 200 && result.status <= 300)
				{
					return result.data;
				}
				else
				{
					//when session is timed out or expired, redirect to login page
					if(result.data && result.data.code == 4403)
					{
						this.discardSession("Session timed out");
						this.redirectToLogin();
						return null;
					}
					
					//if server has responded with some error throw the same
					if(result.data)
					{
						throw result.data;
					}
					
					throw result;
				}
			}
			else
			{
				//for async call result will not be set
				throw {code: 0, message: "Failed to communicate with server"};
			}
		},
		
		/**
		 * Invokes specified url with Http GET method
		 * by passing "paramsObj" attributes as parameters.
		 * @param url Url to be invoked
		 * @param paramsObj params for invocation
		 */
		"invokeGetApi" : function(url, paramsObj) {
			var apiRes = this.invokeApi(
				url, 
				paramsObj, 
				{"methodType": "GET"}
			);
					
			return apiRes;
		},
		
		/**
		 * Invokes specified url with Http DELETE method
		 * by passing "paramsObj" attributes as parameters.
		 * @param url Url to be invoked
		 * @param paramsObj params for invocation
		 */
		"invokeDeleteApi" : function(url, paramsObj) {
			var apiRes = this.invokeApi(
				url, 
				paramsObj, 
				{"methodType": "DELETE"}
			);
					
			return apiRes;
		},
			
		/**
		 * Invokes specified url with Http POST method
		 * by passing "requestObj" as request body.
		 * @param url url to be invoked
		 * @param requestObj Request to be sent
		 */
		"invokePostApi" : function(url, requestObj) {
			var requestBody = requestObj ? JSON.stringify(requestObj) : null;
			
			var apiRes = this.invokeApi(
				url, 
				requestBody, 
				{"methodType": "POST", "contentType" : "application/json"}
			);
					
			return apiRes;
		},
		
		/**
		 * Tries to authenticate the context with specified 
		 * user name and password. On error, exception will be thrown
		 * @param userName User name
		 * @param password Password
		 */
		"authenticate" : function(userName, password) {
			//if invalid user name is provided
    		if(!userName || userName.length == 0)
    		{
    			throw "Please provide valid user name and then try!";
    		}
    		
    		//if invalid password is provided
    		if(!password || password.length == 0)
    		{
    			throw "Please provide valid password and then try!";
    		}
    		
    		//invoke auth api
    		var authResponse = null;
    		
    		try
    		{
	    		authResponse = this.invokePostApi($.appConfiguration.apiBaseUrl + LOGIN_URI,
	    				{"userName":  userName, "password": password}
	    		);
    		}catch(ex)
    		{
    			if(ex.code == 4401)
    			{
    				throw "Authentication failed!\nPlease check your user name and password";
    			}
    			
    			logger.error(ex);
    			throw "Authentication failed!\nAn error occurred while communicating with server!";
    		}
    		
    		//if api invocation resulted in error
			if(authResponse.code != 0)
			{
    			throw "Authentication failed!\nError: " + authResponse.value.message;
			}
			
			//store the auth token in local storage, so that token is available
				// across page refreshes
			localStorage.setItem("authToken", authResponse.authToken);
			this.authToken = authResponse.authToken;
		},
		
		/**
		 * Fetches the actions from server and loads into the internal map. Expected to be called as part
		 * of initialization process.
		 */
		"fetchActions" : function() {
    		var actionsResponse = null;
    		
    		try
    		{
    			actionsResponse = this.invokeGetApi($.appConfiguration.apiBaseUrl + ACTIONS_URI);
    		}catch(ex)
    		{
    			logger.error("An error occurred while fetching actions- " + ex);
    			logger.error(ex);
    			return;
    		}
    		
    		//if api invocation resulted in error
			if(actionsResponse.code != 0)
			{
    			throw "Failed to fetch actions!\nError: " + actionsResponse.value.message;
			}
			
			//load the actions from response on to actions map
			this.actionsMap = {};
			
			for(var i = 0; i < actionsResponse.actions.length; i++)
			{
				this.actionsMap[actionsResponse.actions[i].name] = actionsResponse.actions[i];
			}
		},
		
		"getAction" : function(name) {
			//fetch actions, if they are not already fetched
			if(!this.actionsMap)
			{
				this.fetchActions();
			}
			
			return this.actionsMap[name];
		},
		
		"redirectToLogin" : function() {
			var loginPageMarker = $("#loginPageMarker");

			//check for login page marker in login page
			if(loginPageMarker.length <= 0)
			{
				var currentLocation = $.param(window.location.href);
				window.location.href = $.appConfiguration.loginPageUrl + "?actualPage=" + currentLocation;
			}
			else
			{
				logger.debug("Ignoring redirectToLogin() as currently login page itself is active..");
			}
		},
		
		"discardSession" : function(reason) {
			logger.debug("Discarding session. Reason - " + reason);
			this.authToken = null;
			localStorage.removeItem("authToken");
		},

		/*
		"addScript" : function(path) {
			
			//check if specified script is already added
			if(this.scriptsAdded.indexOf(path) >= 0)
			{
				logger.log("Specified script is already part of context. Ignoring add script request - " + path);
				return;
			}
			
			var sucessFunc = $.proxy(function(){
				//add script entry to context
				this.scriptsAdded[this.scriptsAdded.length] = path;
				console.log("Added dynamic script - " + path);
			}, this);
			
			console.log("Loading script: " + path);
			//load the script
			$.ajax({
				  url: path,
				  dataType: 'script',
				  success: sucessFunc,
				  async: false
			});
		}
		*/
	};
	
	var tokenFromStorage = localStorage.authToken;
	
	if(tokenFromStorage)
	{
		clientContext.authToken = tokenFromStorage;
	}
	else
	{
		clientContext.redirectToLogin();
	}
		
	return clientContext;
}]);

$.application.factory('actionHelper', ['clientContext', function(clientContext){
	var actionHelper = {
		
		/**
		 * Client context maintaining auth tokens, actions etc
		 */
		"clientContext" : clientContext,
		
		/**
		 * Invokes specified action with specified entity and params. For actions based on
		 * GET requestEntity will not be considered
		 */
		"invokeAction" : function(actionName, requestEntity, params) {
			var action = this.clientContext.getAction(actionName);
			
			if(!action)
			{
				throw "Invalid action name specified - " + actionName;
			}
			
			var actionUrl = $.appConfiguration.apiBaseUrl + action.url;
			var expression = new RegExp("");
			
			//if action requires url parameters, ensure all required url parameters are provided
			if(action.urlParameters)
			{
				var urlparamLst = action.urlParameters;
				
				for(var i = 0; i < urlparamLst.length; i++)
				{
					//if any url param is not specified, throw error
					if(!params[urlparamLst[i]])
					{
						throw "Required url-parameter '" + urlparamLst[i] + "' is not specified for invocation of action - " + actionName;
					}
					
					actionUrl = actionUrl.replace(new RegExp("\\{" + urlparamLst[i] + "\\}", "g"), params[urlparamLst[i]]);
					
					delete params[urlparamLst[i]];
				}
				
			}
			
			//invoke the target api url based on action method
			if(action.method == 'GET')
			{
				return this.clientContext.invokeGetApi(actionUrl, params);
			}
			else if(action.method == 'DELETE')
			{
				return this.clientContext.invokeDeleteApi(actionUrl, params);
			}
			else
			{
				if(!action.attachmentsExpected)
				{
					return this.clientContext.invokePostApi(actionUrl, requestEntity);
				}
				
				var files = [];
				var data = null, newData = null;
				
				for(var i = 0; i < action.fileFields.length; i++)
				{
					data = requestEntity[action.fileFields[i]];
					
					//if data is not present
					if(!data)
					{
						continue;
					}
					
					//if single file data is present
					if(!$.isArray(data))
					{
						//if the data represents file info from server
						if(!data.lastModifiedDate)
						{
							continue;
						}
						
						//if file is found add to request as multi part and remove from entity
						files[files.length] = {"name" : action.fileFields[i], "file" : data};
						delete requestEntity[action.fileFields[i]];
						continue;
					}
					
					newData = [];
					
					//if field is an file array
					for(var j = 0; j < data.length; j++)
					{
						//retain non file data
						if(data[j].lastModifiedDate)
						{
							newData[newData.length] = data[j];
							continue;
						}
						
						files[files.length] = {"name" : action.fileFields[i], "file" : data[j]};
					}
					
					//if non file data is present, keep it on entity, if not delete property
					if(newData.length == 0)
					{
						delete requestEntity[action.fileFields[i]];
					}
					else
					{
						requestEntity[action.fileFields[i]] = newData;
					}
				}
				
				//convert entity into json and add it to request
				var request = new FormData();
				requestEntity = JSON.stringify(requestEntity);
				request.append("default", 
					new Blob(
						[requestEntity],
						{type: "application/json"}
					)
				);
				
				for(var i = 0; i < files.length; i++)
				{
					request.append(files[i].name, files[i].file);
				}
				
				//invoke the api
				var apiRes = this.clientContext.invokeApi(
					actionUrl, 
					request, 
					{"methodType": "POST", "multipart" : true}
				);
				
				return apiRes;
			}
		},
		
		/**
		 * Fetches action url for specified action with specified params, so that the resultant url can 
		 * be used in hyperlinks.
		 */
		"actionUrl" : function(actionName, params) {
			var action = this.clientContext.getAction(actionName);
			
			//if invalid action name specified
			if(!action)
			{
				throw "Invalid action name specified - " + actionName;
			}
			
			//if specified action is not GET based action
			if(action.method != 'GET')
			{
				return "Non GET action url requested. Requested action name - " + actionName;
			}

			var actionUrl = $.appConfiguration.apiBaseUrl + action.url;
			var expression = new RegExp("");
			
			//if action requires url parameters, ensure all required url parameters are provided and replace them in result url
			if(action.urlParameters)
			{
				var urlparamLst = action.urlParameters;
				
				for(var i = 0; i < urlparamLst.length; i++)
				{
					//if any url param is not specified, throw error
					if(!params || !params[urlparamLst[i]])
					{
						throw "Required url-parameter '" + urlparamLst[i] + "' is not specified for invocation of action - " + actionName;
					}
					
					actionUrl = actionUrl.replace(new RegExp("\\{" + urlparamLst[i] + "\\}", "g"), params[urlparamLst[i]]);
					
					delete params[urlparamLst[i]];
				}
			}
			
			var queryStringAdded = false;
			
			//if extra params are specified add them to url param list
			if(params)
			{
				actionUrl = actionUrl + "?";
				queryStringAdded = true;
				
				for(var paramName in params)
				{
					actionUrl += paramName + "=" +  params[paramName] + "&";
				}
			}
			
			//add auth token as param
			if(queryStringAdded)
			{
				actionUrl += "&AUTH_TOKEN=" + clientContext.authToken;
			}
			else
			{
				actionUrl += "?AUTH_TOKEN=" + clientContext.authToken;
			}
			
			return actionUrl;
		}
		
	};
	
	return actionHelper;
}]);




