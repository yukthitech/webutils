package com.yukthitech.webutils.common.controllers;

import com.yukthitech.webutils.common.IWebUtilsCommonConstants;
import com.yukthitech.webutils.common.models.BaseResponse;
import com.yukthitech.webutils.common.models.auth.ChangePasswordRequest;
import com.yukthitech.webutils.common.models.auth.LoginCredentials;
import com.yukthitech.webutils.common.models.auth.LoginResponse;
import com.yukthitech.webutils.common.models.auth.ResetPasswordRequest;

/**
 * Controller interface for login and logout.
 * @author akiran
 */
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
	
	/**
	 * Resets the password.
	 * @param resetPassword password details
	 * @return basic response
	 */
	public BaseResponse resetPassword(ResetPasswordRequest resetPassword);
	
	/**
	 * Changes the password.
	 * @param changePassword request object
	 * @return response
	 */
	public BaseResponse changePassword(ChangePasswordRequest changePassword);
}