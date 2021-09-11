/*
 * The MIT License (MIT)
 * Copyright (c) 2015 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.yukthitech.webutils.security;

import static com.yukthitech.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_ACTIVE_USER;
import static com.yukthitech.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_AUTH;
import static com.yukthitech.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_LOGIN;
import static com.yukthitech.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_LOGOUT;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.yukthitech.webutils.IWebUtilsInternalConstants;
import com.yukthitech.webutils.annotations.ActionName;
import com.yukthitech.webutils.annotations.NoAuthentication;
import com.yukthitech.webutils.common.IWebUtilsCommonConstants;
import com.yukthitech.webutils.common.UserDetails;
import com.yukthitech.webutils.common.client.IRequestCustomizer;
import com.yukthitech.webutils.common.controllers.ILoginController;
import com.yukthitech.webutils.common.models.BaseResponse;
import com.yukthitech.webutils.common.models.BasicReadResponse;
import com.yukthitech.webutils.common.models.auth.ChangePasswordRequest;
import com.yukthitech.webutils.common.models.auth.LoginCredentials;
import com.yukthitech.webutils.common.models.auth.LoginResponse;
import com.yukthitech.webutils.common.models.auth.ResetPasswordRequest;
import com.yukthitech.webutils.controllers.BaseController;
import com.yukthitech.webutils.services.CurrentUserService;

/**
 * Controller to perform login operation.
 * @author akiran
 */
@RestController
@RequestMapping(IWebUtilsCommonConstants.AUTH_GROUP_URI)
@ActionName(ACTION_TYPE_AUTH)
public class LoginController extends BaseController implements ILoginController
{
	private static Logger logger = LogManager.getLogger(LoginController.class);
	
	/**
	 * Webapp specific authentication service.
	 */
	@Autowired
	private IAuthenticationService<?> authenticationService;
	
	/**
	 * Service to get current user.
	 */
	@Autowired
	private CurrentUserService currentUserService;

	/**
	 * Current http response.
	 */
	@Autowired
	private HttpServletResponse response;
	
	/**
	 * Current http request.
	 */
	@Autowired
	private HttpServletRequest request;
	
	/**
	 * To manage sessions.
	 */
	@Autowired
	private SessionManagementService sessionManagementService;
	
	@Override
	@NoAuthentication
	@ResponseBody
	@ActionName(ACTION_TYPE_LOGIN)
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public LoginResponse performLogin(@RequestBody @Valid LoginCredentials credentials)
	{
		logger.trace("Trying to peform login operation for user - {}", credentials.getUserName());
		
		UserDetails<?> userDetails = authenticationService.authenticate(credentials.getUserName(), credentials.getPassword(), credentials.getAttributes());
		
		if(userDetails == null)
		{
			logger.error("Authentication failed for user: - {}", credentials.getUserName());
			
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return new LoginResponse(IWebUtilsCommonConstants.RESPONSE_CODE_AUTHENTICATION_ERROR, "Authentication failed!");
		}
		
		logger.trace("Authentication successful");
		
		String sessionToken = sessionManagementService.startSession(userDetails);
		return new LoginResponse(sessionToken, userDetails.getUserId());
	}
	
	@Override
	@NoAuthentication
	@ResponseBody
	@ActionName("resetPassword")
	@RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
	public BaseResponse resetPassword(@RequestBody @Valid ResetPasswordRequest resetPassword)
	{
		authenticationService.resetPassword(resetPassword.getUserName(), resetPassword.getAttributes());
		return new BaseResponse();
	}
	
	@Override
	@NoAuthentication
	@ResponseBody
	@ActionName("changePassword")
	@RequestMapping(value = "/changePassword", method = RequestMethod.POST)
	public BaseResponse changePassword(@RequestBody @Valid ChangePasswordRequest changePassword)
	{
		authenticationService.changePassword(changePassword.getCurrentPassword(), changePassword.getNewPassword());
		return new BaseResponse();
	}

	/**
	 * Fetches current active user details and configuration.
	 * @return Base read response with active user details.
	 */
	@ResponseBody
	@ActionName(ACTION_TYPE_ACTIVE_USER)
	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public BasicReadResponse<UserDetails<?>> activeUser()
	{
		logger.trace("Trying to fetch active user details");
		return new BasicReadResponse<>(currentUserService.getCurrentUserDetails());
	}

	@Override
	@ResponseBody
	@ActionName(ACTION_TYPE_LOGOUT)
	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	public BaseResponse peroformLogout()
	{
		String currentSessionToken = (String) request.getAttribute(IWebUtilsInternalConstants.REQ_ATTR_SESSION_TOKEN);
		
		if(currentSessionToken == null)
		{
			logger.warn("No session token found on request during logout. Ignoring logout call and returning success.");
			return new BaseResponse();
		}
		
		sessionManagementService.clearSession(currentSessionToken);
		return new BaseResponse();
	}
	
	/* (non-Javadoc)
	 * @see com.yukthitech.webutils.common.controllers.IClientController#setRequestCustomizer(com.yukthitech.webutils.common.client.IRequestCustomizer)
	 */
	@Override
	public ILoginController setRequestCustomizer(IRequestCustomizer customizer)
	{
		return null;
	}
}
