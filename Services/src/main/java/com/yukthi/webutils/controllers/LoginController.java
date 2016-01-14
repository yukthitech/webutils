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

package com.yukthi.webutils.controllers;

import static com.yukthi.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_ACTIVE_USER;
import static com.yukthi.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_AUTH;
import static com.yukthi.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_LOGIN;

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

import com.yukthi.webutils.annotations.ActionName;
import com.yukthi.webutils.annotations.NoAuthentication;
import com.yukthi.webutils.common.IWebUtilsCommonConstants;
import com.yukthi.webutils.common.models.ActiveUserModel;
import com.yukthi.webutils.common.models.BasicReadResponse;
import com.yukthi.webutils.common.models.LoginCredentials;
import com.yukthi.webutils.common.models.LoginResponse;
import com.yukthi.webutils.security.ISecurityService;
import com.yukthi.webutils.security.SecurityEncryptionService;
import com.yukthi.webutils.security.UserDetails;
import com.yukthi.webutils.utils.WebUtils;

/**
 * Controller to perform login operation
 * @author akiran
 */
@RestController
@RequestMapping(IWebUtilsCommonConstants.AUTH_GROUP_URI)
@ActionName(ACTION_TYPE_AUTH)
public class LoginController extends BaseController
{
	private static Logger logger = LogManager.getLogger(LoginController.class);
	
	/**
	 * Webapp specific authentication service
	 */
	@Autowired
	private ISecurityService authenticationService;

	/**
	 * Encryption service to generate auth token
	 */
	@Autowired
	private SecurityEncryptionService securityEncryptionService;
	
	/**
	 * Login operation service method. On success, returns auth token that needs to be included
	 * in every request header with name specified by {@link IWebUtilsCommonConstants#HEADER_AUTHORIZATION_TOKEN}.
	 * 
	 * @param credentials Credentials to be used for login
	 * @return On success, returns auth token as part of response
	 */
	@NoAuthentication
	@ResponseBody
	@ActionName(ACTION_TYPE_LOGIN)
	@RequestMapping(value = IWebUtilsCommonConstants.LOGIN_URI_PATH, method = RequestMethod.POST)
	public LoginResponse performLogin(@RequestBody @Valid LoginCredentials credentials, HttpServletResponse response)
	{
		logger.debug("Trying to peform login operation for user - {}", credentials.getUserName());
		
		UserDetails userDetails = authenticationService.authenticate(credentials.getUserName(), credentials.getPassword(), credentials.getAttributes());
		
		if(userDetails == null)
		{
			logger.error("Authentication failed");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return new LoginResponse(IWebUtilsCommonConstants.RESPONSE_CODE_AUTHENTICATION_ERROR, "Authentication failed!");
		}
		
		logger.debug("Authentication successful");
		userDetails.setSessionStartTime(WebUtils.currentTimeInMin());
		return new LoginResponse(securityEncryptionService.encrypt(userDetails));
	}

	/**
	 * Fetches current active user details and configuration
	 * @return
	 */
	@ResponseBody
	@ActionName(ACTION_TYPE_ACTIVE_USER)
	@RequestMapping(value = IWebUtilsCommonConstants.FETCH_USER_PATH, method = RequestMethod.GET)
	public BasicReadResponse<ActiveUserModel> activeUser()
	{
		logger.debug("Trying to fetch active user details");
		
		return new BasicReadResponse<>(authenticationService.getActiverUser());
	}
}
