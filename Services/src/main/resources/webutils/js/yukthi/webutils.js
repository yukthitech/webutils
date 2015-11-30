/**
 * This service provides context behaviour for APIs. Maintains the context
 * across the api calls on client side.
 */
$.application.factory('clientContext', function()
{
	var clientContext = {
		/**
		 * Maintains the auth token that needs to be sent to server
		 * during every api request 
		 */
		"authToken" : null,
		
		/**
		 * Checks and return true if the current context is initialize, that
		 * is current is authenticated
		 */
		"isInitialized" : function() {
			return (this.authToken != null);
		},
		
		/**
		 * Invokes specified url with Http GET method
		 * by passing "paramsObj" attributes as parameters.
		 * @param url Url to be invoked
		 * @param paramsObj params for invocation
		 */
		"invokeGetApi" : function(url, paramsObj) {
			var apiRes = $.makeJsonCall(
					url, 
					paramsObj, 
					{"methodType": "GET"}
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
			var requestBody = JSON.stringify(requestObj);
			
			var apiRes = $.makeJsonCall(
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
    		var authResponse = this.invokePostApi($.appConfiguration.authApiUrl,
    				{"userName":  userName, "password": password}
    		); 
    		
    		//if api invocation resulted in error
			if(authResponse.code != 0)
			{
    			throw "Authentication failed!\nError: " + authResponse.value.message;
			}
			
			//store the auth token in local storage, so that token is available
				// across page refreshes
			localStorage.setItem("authToken", authResponse.authToken);
			this.authToken = authResponse.authToken;
		}
	};
	
	var tokenFromStorage = localStorage.authToken;
	
	if(tokenFromStorage)
	{
		clientContext.authToken = tokenFromStorage;
	}
	else
	{
		var currentLocation = window.location.pathname;
		console.log("Found location '" + currentLocation + "' usage without auth-token");
		debugger;
		
		if(currentLocation != $.appConfiguration.loginPageUrl)
		{
			window.location.href = $.appConfiguration.loginPageUrl;
		}
	}
		
	return clientContext;
});

/*
 * function addCssFile(path) { var head =
 * document.getElementsByTagName('head')[0]; var link =
 * document.createElement('link'); link.rel = 'stylesheet'; link.type =
 * 'text/css'; link.href = path; link.media = 'all'; head.appendChild(link); }
 * 
 * function addJsFile(path) { var head =
 * document.getElementsByTagName('head')[0]; var script =
 * document.createElement('script'); script.type = 'text/javascript'; script.src =
 * path; script.media = 'all'; head.appendChild(script); }
 * 
 * addScript("../webutils/js/jquery-2.1.4.min.js");
 */
