import {$utils, $appConfiguration} from "./common.js";
import {$restService} from "./rest-service.js";
import "../vue-cookies/vue-cookies.js";

/**
 * Manages user authentication, session, and details.
 * @namespace $userService
 */
export var $userService = {

	/**
	 * Authenticates a user against the backend. On successful login, it stores the received authentication token
	 * in `sessionStorage` and as a cookie. If `rememberMe` is enabled, it also caches the login credentials in `localStorage`.
	 * 
	 * @param {object} loginInfo - An object containing login credentials and callbacks.
	 * @param {string} loginInfo.userName - The user's username.
	 * @param {string} loginInfo.password - The user's password.
	 * @param {boolean} loginInfo.rememberMe - If `true`, the credentials will be stored in `localStorage`.
	 * @param {Function} loginInfo.successCallback - A callback executed on successful login.
	 * @param {Function} loginInfo.errorCallback - A callback executed on login failure.
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
	
	/**
	 * Retrieves the user's credentials (`userName`, `password`, `rememberMe`) that were previously cached in `localStorage`
	 * if the "Remember Me" option was selected during a prior login.
	 * 
	 * @returns {object|null} An object with the structure `{ userName, password, rememberMe }` if cached data exists, otherwise `null`.
	 */
	"getLoginInfoFromCache": function() {
		var json = localStorage.getItem("loginInfo");
		
		if(json)
		{
			return JSON.parse(json);
		}

		return null;		
	},
	
	/**
	 * Fetches the details of the currently logged-in user. It first checks for cached details in `sessionStorage`.
	 * If not found, or if `forceRefresh` is true, it makes a REST call to the server.
	 * 
	 * @param {Function} successCallback - A callback executed with the user details object.
	 * @param {Function} errorCallback - A callback executed if fetching details fails.
	 * @param {boolean} [forceRefresh=false] - If `true`, forces a refetch from the server, ignoring any cached data.
	 */
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
	
	/**
	 * Clears all user-related data from the current session, including the auth token and user details from `sessionStorage`.
	 */
	"clearSession": function() {
		$.userDetails = null;
		sessionStorage.removeItem("userDetails");
		sessionStorage.removeItem("authToken");
	},
	
	/**
	 * Logs the current user out by making a call to the backend, clearing the session via `clearSession()`,
	 * removing the auth cookie, and finally invoking the global `$appConfiguration.onLogout()` handler.
	 */
	logout: function()
	{
		var onLogout = function() {
			this.clearSession();
			$cookies.remove('AUTH_TOKEN');
			
			if($appConfiguration.onLogout)
			{
				$appConfiguration.onLogout();
			}
		};

		$restService.invokePost("/api/auth/logout", null, 
			{"context": this, "onSuccess": onLogout, "onError": onLogout, "includeAuthToken": true});
	},

	/**
	 * Checks if the currently logged-in user has one or more of the specified roles.
	 * @param {string} roleStr - A comma-separated string of role names to check (e.g., `"ADMIN,SUPERVISOR"`).
	 * @returns {boolean} `true` if the user has at least one of the specified roles, otherwise `false`.
	 */
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
		
		var roles = roleStr.split(/\s*,\s*/);
		
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