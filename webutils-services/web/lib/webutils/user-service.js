import {$utils, $appConfiguration} from "./common.js";
import {$restService} from "./rest-service.js";
import "https://cdn.jsdelivr.net/npm/vue-cookies@1.8.6/vue-cookies.min.js";

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
	 * @param {string} loginInfo.username - The user's email or mobile (preferred).
	 * @param {string} [loginInfo.mailId] - Legacy alias for username.
	 * @param {string} loginInfo.password - The user's password.
	 * @param {string} loginInfo.userSpace - Custom user space (e.g. jobSeeker, employer).
	 * @param {boolean} loginInfo.rememberMe - If `true`, the credentials will be stored in `localStorage`.
	 * @param {Function} loginInfo.successCallback - A callback executed on successful login.
	 * @param {Function} loginInfo.errorCallback - A callback executed on login failure.
	 */
	authenticate: function(loginInfo)
	{
		var username = loginInfo.username || loginInfo.mailId;
		var rememberMeCache = {
			username: username,
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
			"username": username,
			"password": loginInfo.password,
			"userSpace": loginInfo.userSpace
		}, {"context": this, "onSuccess": onLogin, "onError": onError, "includeAuthToken": false});
	},
	
	/**
	 * Retrieves the user's credentials (`username`, `password`, `rememberMe`) that were previously cached in `localStorage`
	 * if the "Remember Me" option was selected during a prior login.
	 * 
	 * @returns {object|null} An object with the structure `{ username, password, rememberMe }` if cached data exists, otherwise `null`.
	 * Legacy caches that stored `mailId` are normalized to `username`.
	 */
	"getLoginInfoFromCache": function() {
		var json = localStorage.getItem("loginInfo");
		
		if(json)
		{
			var cached = JSON.parse(json);
			if(cached && !cached.username && cached.mailId)
			{
				cached.username = cached.mailId;
			}
			return cached;
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
			$.userDetails = result.response.value;
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

		$restService.invokeGet("/api/user/profile", null, 
				{"context": this, "onSuccess": onUserFetch, "onError": onUserFetchError, "async": false});
	},

	/**
	 * Persists a user preference on the server.
	 *
	 * @param {string} key - Preference key.
	 * @param {*} value - Preference value.
	 * @param {Function} successCallback - Callback executed when preference is saved.
	 * @param {Function} errorCallback - Callback executed on save failure.
	 */
	"setPreference": function(key, value, successCallback, errorCallback) {
		var onSetPreference = $.proxy(function(result) {
			if(this.successCallback)
			{
				this.successCallback(result);
			}
		}, {"successCallback": successCallback});

		var onSetPreferenceError = $.proxy(function(result) {
			if(this.errorCallback)
			{
				this.errorCallback(result);
			}
		}, {"errorCallback": errorCallback});

		$restService.invokePost("/api/user/preference", {
			"key": key,
			"value": value
		}, {"context": this, "onSuccess": onSetPreference, "onError": onSetPreferenceError});
	},

	/**
	 * Fetches a user preference value from server.
	 *
	 * @param {string} key - Preference key.
	 * @param {Function} successCallback - Callback executed with fetched value.
	 * @param {Function} errorCallback - Callback executed on fetch failure.
	 */
	"getPreference": function(key, successCallback, errorCallback) {
		var onGetPreference = $.proxy(function(result) {
			if(this.successCallback)
			{
				this.successCallback(result.response.value);
			}
		}, {"successCallback": successCallback});

		var onGetPreferenceError = $.proxy(function(result) {
			if(this.errorCallback)
			{
				this.errorCallback(result);
			}
		}, {"errorCallback": errorCallback});

		$restService.invokeGet("/api/user/preference/" + encodeURIComponent(key), null,
			{"context": this, "onSuccess": onGetPreference, "onError": onGetPreferenceError});
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
	return $userService.hasRoles(roles);
};

$.userService = $userService;