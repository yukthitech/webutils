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

package com.yukthi.webutils.security;

import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthi.utils.exceptions.InvalidStateException;
import com.yukthi.webutils.IWebUtilsInternalConstants;
import com.yukthi.webutils.WebutilsConfiguration;
import com.yukthi.webutils.common.IWebUtilsCommonConstants;
import com.yukthi.webutils.common.models.BaseResponse;

/**
 * Spring interceptor to control authorization  based on token passed as part of request
 * @author akiran
 */
public class SecurityInterceptor extends HandlerInterceptorAdapter
{
	private static Logger logger = LogManager.getLogger(SecurityInterceptor.class);

	@Autowired
	private SecurityEncryptionService securityEncryptionService;
	
	@Autowired
	private WebutilsConfiguration configuration;
	
	private ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Sends error to client
	 * @param response
	 * @param code
	 * @param message
	 */
	private void sendError(HttpServletResponse response, int code, String message)
	{
		try
		{
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			
			BaseResponse responseObj = new BaseResponse(code, message);
			String responseObjStr = objectMapper.writeValueAsString(responseObj);
			
			OutputStream os = response.getOutputStream();
			IOUtils.write(responseObjStr, os);
			os.close();
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while sending error to response");
		}
	}
	
	/**
	 * Checks if required auth details token is provided and manages expiry of token
	 * @param request Request
	 * @param response Response
	 * @return User details who is trying to invoke the action
	 */
	private UserDetails<?> checkAuthentication(HttpServletRequest request, HttpServletResponse response)
	{
		String authorizationToken = request.getHeader(IWebUtilsCommonConstants.HEADER_AUTHORIZATION_TOKEN);

		//if authorization token is not present
		if(StringUtils.isBlank(authorizationToken))
		{
			logger.debug("No auth token provided in request header");
			sendError(response, IWebUtilsCommonConstants.RESPONSE_CODE_AUTHENTICATION_ERROR, "Authorization failed. No authorization token provided");
			return null;
		}

		
		try
		{
			//decrypt user details
			UserDetails<?> userDetails = securityEncryptionService.decrypt(authorizationToken);
			
			request.setAttribute(IWebUtilsInternalConstants.REQ_ATTR_USER_DETAILS, userDetails);
			response.setHeader(IWebUtilsCommonConstants.HEADER_AUTHORIZATION_TOKEN, userDetails.getAuthToken());
			
			return userDetails;
		}catch(SecurityException ex)
		{
			logger.error("Failed to parse token", ex);
			
			sendError(response, IWebUtilsCommonConstants.RESPONSE_CODE_AUTHENTICATION_ERROR, "Authorization failed. Invalid authorization token provided");
			return null;
		}
	}
	
	/**
	 * Fetches authorization annotation from handler method and checks if the user has sufficient roles to invoke the method
	 * @param userDetails User details who is trying to invoke the action
	 * @param handlerMethod Action method being invoked
	 * @param response Response
	 * @return True if user is authorized to invoke action
	 */
	private boolean isAuthorized(UserDetails<?> userDetails, HandlerMethod handlerMethod, HttpServletResponse response)
	{
		try
		{
			Annotation authAnnotation = handlerMethod.getMethodAnnotation(configuration.getAuthorizationAnnotation());
			
			//if handler does not need any authorization, proceed with the flow
			if(authAnnotation == null)
			{
				return true;
			}
			
			Method valueMethod = configuration.getAuthorizationAnnotation().getMethod("value");
			Object roles[] = (Object[])valueMethod.invoke(authAnnotation);
			
			//if no roles are defined
			if(roles == null || roles.length == 0)
			{
				return true;
			}
			
			Set<?> userRoles = userDetails.getRoles();
			
			//if at least one of the matching role is present return true
			for(Object role : roles)
			{
				if(userRoles.contains(role))
				{
					return true;
				}
			}
			
			//if no matching roles are found return error
			sendError(response, IWebUtilsCommonConstants.RESPONSE_CODE_AUTHORIZATION_ERROR, "Authorization failed. User is not authorized to invoke current action.");			
			return false;
		}catch(Exception ex)
		{
			logger.error("An error occurred while checking for authorization", ex);
			throw new IllegalStateException("An error occurred while checking for authorization");
		}
	}

	/**
	 * Spring prehandle method, which is used to check authorization
	 * @param request Request
	 * @param response Response
	 * @param handler Handler method
	 * @return true, if authorized
	 * @throws Exception
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
	{
		//if the api call is for authentication, dont perform any authorization check
		if(request.getRequestURI().endsWith(IWebUtilsCommonConstants.LOGIN_URI))
		{
			return true;
		}
		
		//if auth is disable for the webapp
		if(!configuration.isEnableAuth())
		{
			return true;
		}

		//check and validate authorization token
		UserDetails<?> userDetails = checkAuthentication(request, response);
		
		if(userDetails == null)
		{
			return false;
		}

		//if the current user is not authorized to access the handler method
		if(!isAuthorized(userDetails, (HandlerMethod)handler, response))
		{
			return false;
		}
		
		return true;
	}

}
