var LOGIN_URI = "/auth/login";
var ACTIONS_URI = "/actions/fetch";
var URL_PARAM_PATTERN = /\{(\w+)\}/g;

var ALERTS_DLG_ID = "webutilsAlertDialog";
var CONFIRM_DLG_ID = "webutilsConfirmDialog";
var INFO_BOX_ID = "webutilsInfoBox";
var IN_PRGORESS_DLG_ID = "webutilsInProgressDialog";

var IN_PRGORESS_STATE_HIDDEN = 0;
var IN_PRGORESS_STATE_SHOWING = 1;
var IN_PRGORESS_STATE_SHOWN = 2;
var IN_PRGORESS_STATE_HIDING = 3;


$.currentScopeId = 0;

$.nextScopeId = function() {
	$.currentScopeId++;
	return $.currentScopeId;
};

$.fontWidth = function(font, text) {
	// re-use canvas object for better performance
    var canvas = $.fontCharWidth_canvas || ($.fontCharWidth_canvas = document.createElement("canvas"));
    var context = canvas.getContext("2d");
    context.font = font;
    var metrics = context.measureText(text);
    return metrics.width;
}

$.application.controller('webutilsCommonController', ["$scope", "utils", function($scope, utils) {
	
	$scope.closeAlert = function() {
		//ensure call back is called after proper closing of dialog. This is needed for
			//	nested dialogs
		$('#' + ALERTS_DLG_ID).off('hidden.bs.modal').on('hidden.bs.modal', function (e) {
			if(utils.callback)
			{
				utils.callback();
			}
		});

		$('#' + ALERTS_DLG_ID).modal('hide');
	};
	
	$scope.closeConfirm = function() {
		$('#' + CONFIRM_DLG_ID).off('hidden.bs.modal').on('hidden.bs.modal', function (e) {
			if(utils.callback)
			{
				utils.callback(true);
			}
		});

		$('#' + CONFIRM_DLG_ID).modal('hide');
	};
	
	$scope.cancelConfirm = function() {
		$('#' + CONFIRM_DLG_ID).off('hidden.bs.modal').on('hidden.bs.modal', function (e) {
			if(utils.callback)
			{
				utils.callback(false);
			}
		});

		$('#' + CONFIRM_DLG_ID).modal('hide');
	};
	
	$scope.closeInfo = function() {
		$('#' + INFO_BOX_ID).css('display', "none");
	};

}]);

$.modalManager = {
	"modalStack": [],
	
	"openModal": function(id, config) {
		var dlg = $("#" + id);
		
		if(dlg.length < 0)
		{
			throw "No modal found with specified id - " + id;
		}
		
		$('#' + id).off('shown.bs.modal').on('shown.bs.modal', $.proxy(function (e) {
			this.modalStack.push(this.id);
			
			if(this.config && this.config.onShow)
			{
				var onShow = this.config.onShow;
				
				if(this.config.context)
				{
					onShow = $.proxy(onShow, this.config.context);
				}
				
				onShow();
			}
		}, {"modalStack": this.modalStack, "config": config, "id": id}));

		$('#' + id).off('hidden.bs.modal').on('hidden.bs.modal', $.proxy(function (e) {
			var idx = this.modalStack.indexOf(this.id);
			
			if(idx < 0)
			{
				return;
			}
			
			this.modalStack.splice(idx, 1);
			
			if(this.modalStack.length > 0)
			{
				$('body').addClass('modal-open');
			}
			
			if(this.config && this.config.onHide)
			{
				var onHide = this.config.onHide;
				
				if(this.config.context)
				{
					onHide = $.proxy(onHide, this.config.context);
				}
				
				onHide();
			}
		}, {"modalStack": this.modalStack, "config": config, "id": id}));
		
		$('#' + id).modal('show');
	}
};


$.application.factory('utils', [function(){
	var utils = {
		"ARG_PATERN" : /\{\}/g,
		"callback": null,
		"firstAlert": true,
		"firstConfirm": true,
		
		"inProgressDisplayed": false,
		"inProgressState": IN_PRGORESS_STATE_HIDDEN,
		"inProgressFirstTime": true,
		"inProgressCheckId": null,

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
			this.callback = callback;
			
			$('#' + ALERTS_DLG_ID + ' .modal-body').html(message);
			
			this.openModal(ALERTS_DLG_ID, {
				onShow: function() {
					$('#' + ALERTS_DLG_ID + " .btn-primary").focus();
				}
			});
		},

		/*
		 * As info is closed by default by time and also can be closed by close button, in timer
		 * function displayTime is used, to ensure new infos are not getting closed because of old timer
		 */
		"info" : function(message) {
			message = $.isArray(message) ? this.format(message[0], message, 1) : message;
			
			//set content and display			
			$("#" + INFO_BOX_ID + " .content").html(message);
			$("#" + INFO_BOX_ID).css("display", "block");
			
			//set display time on message
			var displayTime = "" + (new Date()).getMilliseconds();
			$("#" + INFO_BOX_ID).data("displayTime", displayTime);
			
			//get timeout period
			var time = $.appConfiguration.infoTimeOut ? $.appConfiguration.infoTimeOut : 3;
			
			//auto timer after which info should auto close
			setTimeout($.proxy(function() {
				var infoDiv = $("#" + INFO_BOX_ID);
				
				if(infoDiv.data("displayTime") != this.displayTime)
				{
					return;
				}
				
				$("#" + INFO_BOX_ID).css("display", "none");
			}, {"displayTime": displayTime}), (time * 1000));
		},
		
		"confirm" : function(message, callback) {
			message = $.isArray(message) ? this.format(message[0], message, 1) : message;
			this.callback = callback;

			$('#' + CONFIRM_DLG_ID + ' .modal-body').html(message);
			
			this.openModal(CONFIRM_DLG_ID, {
				onShow: function() {
					$('#' + CONFIRM_DLG_ID + " .btn-primary").focus();
				}
			});
		},
		
		/**
		 * A dialog can be opened only when it is properly closed and similarly a dialog can be closed only when it is properly
		 * opened. Since it can not be done synchronously following logic is employed
		 * 		1) displayInProgress & hideInProgress => will toggle the flag  "inProgressDisplayed"
		 * 		2) For first time when display is requested open and close listeners are added to dialog.
		 * 		3) Whenever dialog is opened a background thread is created to monitor "inProgressDisplayed" flag. When it becomes
		 * 				false, dialog will be closed. "inProgressState" is used to ensure open and close is not called multiple times.
		 * 		4) During close event, the thread started during open is stopped. A one time check thread is created which would check
		 * 				after full close, if the "inProgressDisplayed" is true, then dialog will be reopened.
		 */
		"displayInProgress" : function() {
			this.inProgressDisplayed = true;
			
			//if the progress is hidden and progress dialog is available in dom
			if(this.inProgressState == IN_PRGORESS_STATE_HIDDEN && $('#' + IN_PRGORESS_DLG_ID).length > 0)
			{
				//change the status
				this.inProgressState = IN_PRGORESS_STATE_SHOWING;

				this.openModal(IN_PRGORESS_DLG_ID, {
					context: this,
					
					/*
					 * after dialog is shown, at specific intervals check for flag and close the dialog 
					 */
					onShow: function() {
						//change the state
						this.inProgressState = IN_PRGORESS_STATE_SHOWN;
						
						//background thread which will check for flag at regular intervals
						this.inProgressCheckId = setInterval($.proxy(function(){
							if(!this.inProgressDisplayed && this.inProgressState == IN_PRGORESS_STATE_SHOWN)
							{
								this.inProgressState = IN_PRGORESS_STATE_HIDING;
								$('#' + IN_PRGORESS_DLG_ID).modal('hide');
							}
						}, this),  100);
					},
					
					/*
					 * After dialog is closed, add listener to check if the in progress dialog is requested to display,
					 * if so display it
					 */
					onHide: function() {
						//kill thread which was started when dialog was shown
						clearInterval(this.inProgressCheckId);
						
						//change the state
						this.inProgressState = IN_PRGORESS_STATE_HIDDEN;
						this.inProgressCheckId = null;
						
						//add one time check thread, which would check for flag and display dialog if needed
						setTimeout($.proxy(function(){
							if(this.inProgressDisplayed && this.inProgressState == IN_PRGORESS_STATE_HIDDEN)
							{
								this.inProgressState = IN_PRGORESS_STATE_SHOWING;
								$('#' + IN_PRGORESS_DLG_ID).modal('show');
							}
						}, this), 100);
					}
				});
				
			}
		},

		"hideInProgress" : function() {
			this.inProgressDisplayed = false;
		},

		/**
		 * Executes list of function specified by "functionLst" in a sequential order. During every function call
		 * a callback method is passed, which needs to be invoked by the executing function at end, to execute the next
		 * step function in the flow. If callback method is invoked with arguments, those arguments are passed to next method
		 * from second argument.
		 * 
		 * This helps greatly in executing ajax based functions in sequential order.
		 * 
		 * As a first argument a context is accepted. All the functions are executed as part of the same context. So that data
		 * can be shared between the functions using simple "this."
		 */
		"executeAsyncSteps" : function(context, functionLst) {
			
			//if context is not defined, use empty object
			if(!context)
			{
				context = {};
			}
			
			var invokeContext = {
				"context": context, 
				"functionLst": functionLst, 
				"index": -1, 
			};
			
			var invokeNextFunction = $.proxy(function() {
				this.index++;
				
				if(this.index >= functionLst.length)
				{
					return;
				}
				
				//wrap the target function using proxy, under specified context
				var func = $.proxy(this.functionLst[this.index], this.context);
				
				//build function invocation script which flattens current method arguments
					// into secondary arguments
				var funcInvokeStr = 'func(this.invokeNextFunction';
				
				if(arguments && arguments.length > 0)
				{
					for(var i = 0; i < arguments.length; i++)
					{
						funcInvokeStr += ", arguments[" + i + "]";
					}
				}
				
				funcInvokeStr += ")";
				
				//execute function invocation string
				eval(funcInvokeStr);
			}, invokeContext);
			
			invokeContext.invokeNextFunction = invokeNextFunction;
			
			//invoke first function
			invokeNextFunction();
		},
	};
	
	//copy modal manager functionality into utils
	$.extend(utils, $.modalManager);

	return utils;
}]);

$.application.factory('logger', ["utils", function(utils){
	var logger = {

		"debugEnabled" : ($.appConfiguration.debugEnabled ? true : false),
		"errorEnabled" : ($.appConfiguration.errorEnabled ? true : false),
		"traceEnabled" : ($.appConfiguration.traceEnabled ? true : false),

		"log": function(prefix, message, args) {
			//if message is not an object (like error etc), print the object directly
			if((typeof message) != 'string')
			{
				console.log(prefix);
				console.log(message);
				return;
			}
			
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
			
			//if message is not an object (like error etc), print the object directly
			if((typeof message) != 'string')
			{
				console.log("ERROR");
				console.error(message);
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
$.application.factory('clientContext', ['logger', 'utils', function(logger, utils) {
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
		
		"invokeApi" : function(url, data, config, callback) {

			var methodType = (config && config.methodType)? config.methodType: "POST";
			var contentType = (config && config.contentType)? config.contentType : 'application/x-www-form-urlencoded; charset=UTF-8';
			var processData = undefined;
			var asynchronous = (config && (config.async == false))? false : true;
			
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
			
			var currentContext = this;
			
			var result = {
				"data" : null,
				status : null,
				error: null,
				backgroundApi : (config && config.backgroundApi),
				newAuthToken : null,
				clientContext : currentContext,
				"callback": callback,
				
				"utils": utils,
			};
			
			utils.displayInProgress();
			
			$.ajax({
				  type: methodType,
				  dataType: 'json',
				  url: url,
				  data: data,
				  async: asynchronous,
				  cache: false,
				  "contentType": contentType,
				  "processData": processData,
				  "headers": headers,
				  
				  success: $.proxy(function(resData, textStatus, jqXHR){
					  	utils.hideInProgress();
					  
						//if this is a normal api (not part of background api), then only
					  		//save updated auth header
						if(!this.backgroundApi)
						{
							var newAuthToken = jqXHR.getResponseHeader("AUTH_TOKEN");
							
							if(newAuthToken && newAuthToken != this.clientContext.authToken)
							{
								logger.debug("New auth token recieved");
								this.clientContext.authToken = newAuthToken;
								localStorage.setItem("authToken", newAuthToken);
							}
						}
						
						this.callback(resData, {
							"statusCode": jqXHR.statusCode().status,
							"success" : true,
							"error": false,
							"responseCode": resData.code
						});
					  
				  }, result),
				  
				  error: $.proxy(function(jqXHR, textStatus, errorThrown){
					utils.hideInProgress();
					var resData = null;
					  
					try
					{
						resData = $.parseJSON(jqXHR.responseText);
						logger.error("Got error as - {}", resData);
					}catch(ex)
					{
						logger.error("Failed to parsed error response text as json. Error - {}", ex);
						logger.error("Error Response text: " + jqXHR.responseText);
						
						this.callback(null, {
							"statusCode": jqXHR.statusCode().status,
							"success" : false,
							"error": true,
							"responseCode": -1
						});
						
						return;
					}
					
					try
					{
						this.callback(resData, {
							"statusCode": jqXHR.statusCode().status,
							"success" : false,
							"error": true,
							"responseCode": resData.code
						});
					}catch(ex) 
					{
						logger.error("An error occurred while calling AJAX callback method - {}", ex);
						logger.error(ex);
					}
					
					var status = jqXHR.statusCode().status;
						
					//when session is timed out or expired, redirect to login page
					if(resData && resData.code == 4403)
					{
						this.clientContext.discardSession("Session timed out");
						this.clientContext.redirectToLogin();
						return null;
					}
				}, result)
			});
		},
		
		/**
		 * Invokes specified url with Http GET method
		 * by passing "paramsObj" attributes as parameters.
		 * @param url Url to be invoked
		 * @param paramsObj params for invocation
		 */
		"invokeGetApi" : function(url, paramsObj, callback, config) {
			var apiRes = this.invokeApi(
				url, 
				paramsObj, 
				{
					"methodType": "GET", 
					"async": (config && (config.async == false)) ? false : true
				},
				callback
			);
					
			return apiRes;
		},
		
		/**
		 * Invokes specified url with Http DELETE method
		 * by passing "paramsObj" attributes as parameters.
		 * @param url Url to be invoked
		 * @param paramsObj params for invocation
		 */
		"invokeDeleteApi" : function(url, paramsObj, callback, config) {
			var apiRes = this.invokeApi(
				url, 
				paramsObj, 
				{
					"methodType": "DELETE", 
					"async": (config && (config.async == false)) ? false : true
				},
				callback
			);
					
			return apiRes;
		},
			
		/**
		 * Invokes specified url with Http POST method
		 * by passing "requestObj" as request body.
		 * @param url url to be invoked
		 * @param requestObj Request to be sent
		 */
		"invokePostApi" : function(url, requestObj, callback, config) {
			var requestBody = requestObj ? JSON.stringify(requestObj) : null;
			
			var apiRes = this.invokeApi(
				url, 
				requestBody, 
				{
					"methodType": "POST", 
					"contentType" : "application/json", 
					"async": (config && (config.async == false)) ? false : true
				},
				callback
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
    		
    		var clientContext = this;
    		var error = null;
    		
    		this.invokePostApi(
				$.appConfiguration.apiBaseUrl + LOGIN_URI,
				{"userName":  userName, "password": password},
				
				function(resData, config) {
					
					//if authentication error occurs
	    			if(config.responseCode == 4401)
	    			{
	    				error = "Authentication failed!\nPlease check your user name and password";
	    				return;
	    			}
	    			
	    			if(config.responseCode != 0)
	    			{
	    				error = "Authentication failed!\nError: " + resData.message;
	    				return;
	    			}
	    			
	    			//store the auth token in local storage, so that token is available
					// across page refreshes
					localStorage.setItem("authToken", resData.authToken);
					clientContext.authToken = authResponse.authToken;
				},
				
				{"async": false}
    		);
    		
    		if(error)
    		{
    			throw error;
    		}
		},
		
		/**
		 * Fetches the actions from server and loads into the internal map. Expected to be called as part
		 * of initialization process.
		 */
		"fetchActions" : function() {
    		var actionsResponse = null;
    		
    		this.invokeGetApi(
    			$.appConfiguration.apiBaseUrl + ACTIONS_URI,
    			null,
    			
    			function(resData, config) {
    	    		//if api invocation resulted in error
    				if(config.responseCode != 0)
    				{
    	    			throw "Failed to fetch actions!\nError: " + resData.message;
    				}
    				
    				actionsResponse = resData;
    			},
    			
    			{"async" : false}
    		);
			
			//load the actions from response on to actions map
			this.actionsMap = {};
			
			logger.debug("Got actions of length - {}", actionsResponse.actions.length);
			
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
		"invokeAction" : function(actionName, requestEntity, params, callback) {
			var action = this.clientContext.getAction(actionName);
			
			if(!action)
			{
				throw "Invalid action name specified - " + actionName;
			}
			
			//checks if the specified value is an object or not
			var isObject = function(value) {
				//ignore null
				if(!value)
				{
					return false;
				}
				
				//non objects ignore
				if((typeof value) != 'object')
				{
					return false;
				}
				
				//if object ensure at least one property is present
				var count = 0;
				
				//if at least one property is found return true
				for(var name in value)
				{
					if(count > 0)
					{
						return true;
					}
					
					//sometimes undefined comes on all object, ignore such cases
					if(name)
					{
						count++;
					}
				}
				
				return false;
			}
			
			//function which filter array elements
			var filterArray = function(arr) {
				var arrClone = [];
				var val = null;
				
				for(var i = 0; i < arr.length; i++)
				{
					//filter the array element
					val = filterEntity(arr[i]);
					
					//ignore filtered null values
					if(val == null || val == undefined)
					{
						continue;
					}
					
					arrClone[arrClone.length] = val;
				}
				
				//if array is empty return null
				if(arrClone.length == 0)
				{
					return null;
				}
				
				return arrClone;
			};
			
			//function which recursively removes null, empty objects. And also remove properties having $ (which angular js injects)
			var filterEntity = function(obj) {
				//if value is null, return null
				if(obj == null || obj == undefined)
				{
					return null;
				}
				
				//if value is not object
				if(!isObject(obj))
				{
					//check if it is an array
					if($.isArray(obj))
					{
						return filterArray(obj);
					}
					
					//not objects simply return
					return obj;
				}
				
				//holds final result object
				var propValue = null;
				
				//loop through prop names
				for(var name in obj)
				{
					//exclude properties which has $ in them
					if(name.indexOf("$") >= 0)
					{
						delete obj[name];
						continue;
					}

					//recursively filter objects
					propValue = filterEntity(obj[name]);
					
					//ignore null, which can be the case after filtering
					if(propValue == null || propValue == undefined)
					{
						delete obj[name];
						continue;
					}
				}
				
				//if result is empty return null
				if($.isEmptyObject(obj))
				{
					return null;
				}
				
				return obj;
			};
			
			requestEntity = filterEntity(requestEntity);
			
			var actionUrl = $.appConfiguration.apiBaseUrl + action.url;
			var expression = new RegExp("");
			
			//if action requires url parameters, ensure all required url parameters are provided
			if(action.urlParameters)
			{
				var urlparamLst = action.urlParameters;
				
				for(var i = 0; i < urlparamLst.length; i++)
				{
					//if any url param is not specified, throw error
					if(params[urlparamLst[i]] == null || params[urlparamLst[i]] == undefined)
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
				return this.clientContext.invokeGetApi(actionUrl, params, callback);
			}
			else if(action.method == 'DELETE')
			{
				return this.clientContext.invokeDeleteApi(actionUrl, params, callback);
			}
			else
			{
				if(!action.attachmentsExpected)
				{
					return this.clientContext.invokePostApi(actionUrl, requestEntity, callback);
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
						if(!data[j].lastModifiedDate)
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
					{"methodType": "POST", "multipart" : true},
					callback
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


$.application.factory('modelDefService', ["actionHelper", function(actionHelper){
	var modelDefService = {

		"modelDefMap" : {},
		"searchQueryDefMap" : {},

		"getModelDef": function(name, callback) {
			if(this.modelDefMap[name])
			{
				callback({"modelDef": this.modelDefMap[name]}, {"success": true, "error": false});
				return;
			}
			
			actionHelper.invokeAction('modelDef.fetch', null, {"name": name}, $.proxy(function(modelDefResp, respConfig){
				if(!modelDefResp.modelDef)
				{
					throw "Failed to fetch model def with name - " + this.modelName;
				}
				
				this.modelDefService.modelDefMap[this.modelName] = modelDefResp.modelDef;
				this.callback(modelDefResp, respConfig);
			}, {"modelDefService": this, "callback": callback, "modelName": name} ));
		},

		"getSearchQueryDef": function(name, callback) {
			if(this.searchQueryDefMap[name])
			{
				callback({"modelDef": this.searchQueryDefMap[name]}, {"success": true, "error": false});
				return;
			}
			
			actionHelper.invokeAction('search.fetch.queryDef', null, {"name": name}, $.proxy(function(modelDefResp, respConfig){
				if(!modelDefResp.modelDef)
				{
					throw "Failed to fetch searh query model def with name - " + this.modelName;
				}
				
				this.modelDefService.searchQueryDefMap[this.modelName] = modelDefResp.modelDef;
				this.callback(modelDefResp, respConfig);
			}, {"modelDefService": this, "callback": callback, "modelName": name} ));
		}
		
	};
	
	return modelDefService;
}]);


