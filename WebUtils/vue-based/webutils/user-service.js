import {$utils, $appConfiguration} from "./common.js";
import {$restService} from "./rest-service.js";
import "../vue-cookies/vue-cookies.js";

export var $userService = {

	/**
	 * Used to authenticate the user and handle post login work
	 * setting token in storage and cookie.
	 * Param loginInfo -  userName, password, rememberMe, successCallback, errorCallback 
	 */
	authenticate: function(loginInfo)
	{
		var rememberMeCache = {
			userName: loginInfo.userName,
			password: loginInfo.password,
			rememberMe: loginInfo.rememberMe
		};
		
		if(loginInfo.rememberMe)
		{
			localStorage.setItem("loginInfo", JSON.stringify(rememberMeCache));
		}
		else
		{
			localStorage.removeItem("loginInfo");
		}
		
		var onLogin = function(result) {
			sessionStorage.setItem("authToken", result.response.authToken);
			$cookies.set('AUTH_TOKEN', result.response.authToken);
			//$cookies.put("AUTH_TOKEN", result.response.authToken, {"path": "/"});
			
			if(loginInfo.successCallback)
			{
				loginInfo.successCallback(result);
			}
		};
		
		var onError = function(data) {
			if(data.statusCode == 401)
			{
				$utils.alert("Authentication failed!<br/>Please correct your username and password and try again.");

				if(loginInfo.errorCallback)
				{
					loginInfo.errorCallback();
				}
				
				return;
			}
			
			$utils.alert("Server error occurred!<br/>Please contact administrator if error persists.");
			
			if(loginInfo.errorCallback)
			{
				loginInfo.errorCallback(data);
			}
		};

		// remove current user details if any		
		sessionStorage.removeItem("userDetails");
		
		$restService.invokePost("/api/auth/login", {
			"userName": loginInfo.userName,
			"password": loginInfo.password
		}, {"context": this, "onSuccess": onLogin, "onError": onError, "includeAuthToken": false});
	},
	
	"getLoginInfoFromCache": function() {
		var json = localStorage.getItem("loginInfo");
		
		if(json)
		{
			return JSON.parse(json);
		}

		return null;		
	},
	
	"getUserDetails": function(successCallback, errorCallback, forceRefresh) {
		var json = forceRefresh ? null : sessionStorage.getItem("userDetails");
		
		if(json)
		{
			$.userDetails = JSON.parse(json);
			
			if(successCallback)
			{
				successCallback($.userDetails);
			}
			
			return;
		}
		
		var onUserFetch = $.proxy(function(result) {
			$.userDetails = result.response.model;
			sessionStorage.setItem("userDetails", JSON.stringify($.userDetails));
			
			if(this.successCallback)
			{
				this.successCallback($.userDetails);
			}
		}, {"successCallback": successCallback});
		
		var onUserFetchError = $.proxy(function(result) {
			if(this.errorCallback)
			{
				this.errorCallback();
			}
		}, {"errorCallback": errorCallback});

		$restService.invokeGet("/api/auth/user", null, 
				{"context": this, "onSuccess": onUserFetch, "onError": onUserFetchError, "async": false});
	},
	
	"clearSession": function() {
		$.userDetails = null;
		sessionStorage.removeItem("userDetails");
		sessionStorage.removeItem("authToken");
	},
	
	logout: function()
	{
		var onLogout = function() {
			this.clearSession();
			$cookies.remove('AUTH_TOKEN');
			
			$appConfiguration.onLogout();
		};

		$restService.invokePost("/api/auth/logout", null, 
			{"context": this, "onSuccess": onLogout, "onError": onLogout, "includeAuthToken": true});
	},

	"hasRoles": function(roleStr) {
		if(!roleStr)
		{
			return true;
		}
		
		if(!$.userDetails)
		{
			this.getUserDetails();
			
			if(!$.userDetails)
			{
				return false;
			}
		}
		
		var roles = roleStr.split(/\s*\,\s*/);
		
		for(var role of roles)
		{
			if($.userDetails.roles.indexOf(role) >= 0)
			{
				return true;
			}
		}
		
		return false;
	}
};



export function $hasRoles(roles)
{
	return $.userService.hasRoles(roles);
};
