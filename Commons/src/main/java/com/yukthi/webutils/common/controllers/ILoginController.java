package com.yukthi.webutils.common.controllers;

import com.yukthi.webutils.common.IWebUtilsCommonConstants;
import com.yukthi.webutils.common.RemoteService;
import com.yukthi.webutils.common.models.LoginCredentials;
import com.yukthi.webutils.common.models.LoginResponse;

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
	LoginResponse performLogin(LoginCredentials credentials);

}