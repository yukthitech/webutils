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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthi.utils.exceptions.InvalidStateException;
import com.yukthi.webutils.IWebUtilsInternalConstants;
import com.yukthi.webutils.WebutilsConfiguration;
import com.yukthi.webutils.common.ICommonConstants;
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
	
	private boolean checkAuthentication(HttpServletRequest request, HttpServletResponse response)
	{
		String authorizationToken = request.getHeader(ICommonConstants.HEADER_AUTHORIZATION_TOKEN);

		//if authorization token is not present
		if(StringUtils.isBlank(authorizationToken))
		{
			logger.debug("No auth token provided in request header");
			sendError(response, ICommonConstants.RESPONSE_CODE_AUTHENTICATION_ERROR, "Authorization failed. No authorization token provided");
			return false;
		}

		
		try
		{
			//decrypt user details
			UserDetails<?> userDetails = securityEncryptionService.decrypt(authorizationToken);
			
			request.setAttribute(IWebUtilsInternalConstants.REQ_ATTR_USER_DETAILS, userDetails);
			response.setHeader(ICommonConstants.HEADER_AUTHORIZATION_TOKEN, userDetails.getAuthToken());
		}catch(SecurityException ex)
		{
			logger.error("Failed to parse token", ex);
			
			sendError(response, ICommonConstants.RESPONSE_CODE_AUTHENTICATION_ERROR, "Authorization failed. No authorization token provided");
			return false;
		}
		
		return true;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
	{
		//if auth is diable for the webapp
		if(!configuration.isEnableAuth())
		{
			return true;
		}
		
		if(!checkAuthentication(request, response))
		{
			return false;
		}
		
		//TODO: Perform authorization here 
		return true;
	}

}
