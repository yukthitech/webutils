package com.yukthitech.webutils.common.controllers;

import com.yukthitech.webutils.common.IWebUtilsCommonConstants;
import com.yukthitech.webutils.common.RemoteService;
import com.yukthitech.webutils.common.models.BaseResponse;
import com.yukthitech.webutils.common.models.LoginCredentials;
import com.yukthitech.webutils.common.models.LoginResponse;

/**
 * Controller interface for login and logout.
 * @author akiran
 */
@RemoteService
public interface ILoginController extends IClientController<ILoginController>
{

	/**
	 * Login operation service method. On success, returns auth token that needs to be included
	 * in every request header with name specified by {@link IWebUtilsCommonConstants#HEADER_AUTHORIZATION_TOKEN}.
	 * 
	 * @param credentials Credentials to be used for login
	 * @return On success, returns auth token as part of response
	 */
	public LoginResponse performLogin(LoginCredentials credentials);

	/**
	 * Logs out or invalidates the current session.
	 * @return Success/failure response.
	 */
	public BaseResponse peroformLogout();
}