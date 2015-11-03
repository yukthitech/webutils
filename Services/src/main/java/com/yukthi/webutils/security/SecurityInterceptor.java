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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthi.utils.CryptoUtils;
import com.yukthi.utils.exceptions.InvalidStateException;
import com.yukthi.webutils.IWebUtilsInternalConstants;
import com.yukthi.webutils.common.ICommonConstants;
import com.yukthi.webutils.common.models.BaseResponse;

/**
 * @author akiran
 *
 */
public class SecurityInterceptor extends HandlerInterceptorAdapter
{
	private static Logger logger = LogManager.getLogger(SecurityInterceptor.class);

	private ObjectMapper objectMapper = new ObjectMapper();
	
	@Autowired
	private IAuthenticationService<?> authenticationService;

	private Map<Object, Integer> roleToId = new HashMap<>();
	private Map<String, Object> idToRole = new HashMap<>();
	
	private String secretKey;
	
	private long sessionTime = 3;
	
	public SecurityInterceptor(Class<? extends Enum<?>> roleType, String secretKey)
	{
		Enum<?> enumValues[] = roleType.getEnumConstants();
		
		for(Enum<?> e : enumValues)
		{
			roleToId.put(e, e.ordinal());
			idToRole.put("" + e.ordinal(), e);
		}
		
		this.secretKey = secretKey;
	}
	
	/**
	 * @param sessionTime the {@link #sessionTime sessionTime} to set
	 */
	public void setSessionTime(long sessionTime)
	{
		this.sessionTime = sessionTime;
	}
	
	/**
	 * Generates encrypted string with specified user if and roles
	 * @param userId
	 * @param roles
	 * @return
	 */
	protected String encrypt(long userId, Object roles[])
	{
		if(roles == null)
		{
			throw new InvalidStateException("No roles found for user - {}", userId);
		}
		
		//get current time in minutes
		long time = System.currentTimeMillis() / 60000L;
		
		StringBuilder builder = new StringBuilder();
		
		//add current time stamp, user-id and roles
		builder.append(time).append(":");
		builder.append(userId).append(":");
		
		for(Object role : roles)
		{
			//if invalid role is specified
			if(!this.roleToId.containsKey(role))
			{
				throw new InvalidStateException("Invalid role encountered - {}. Expected roles - {}", role, this.roleToId.keySet());
			}
			
			builder.append(this.roleToId.get(role)).append(",");
		}
		
		//delete trailing comma
		builder.deleteCharAt(builder.length() - 1);
		
		String userWithRoles = builder.toString();
		return time + "-" + CryptoUtils.encrypt(secretKey, userWithRoles);
	}
	
	protected UserDetails<?> decrypt(String encryptedString, HttpServletResponse response)
	{
		int idx = encryptedString.indexOf("-");
		
		if(idx <= 0)
		{
			logger.debug("Invalid auth token. No - found");
			return null;
		}
		
		String time = encryptedString.substring(0, idx);
		encryptedString = encryptedString.substring(idx + 1);
		
		String decryptedStr = CryptoUtils.decrypt(secretKey, encryptedString);
		Pattern ENC_PATTERN = Pattern.compile("(\\d+)\\:(\\d+):([\\d\\,]+)");
		Matcher matcher = ENC_PATTERN.matcher(decryptedStr);
		
		if(!matcher.matches())
		{
			logger.debug("Invalid auth token. Invalid pattern");
			return null;
		}
		
		String time1 = matcher.group(1);
		
		if(!time1.equals(time))
		{
			logger.debug("Invalid auth token. Time stamp did not match");
			return null;
		}
		
		//get current time in minutes
		long currentTime = System.currentTimeMillis() / 60000L;
		long reqTime = Long.parseLong(time1);
		long diff = currentTime - reqTime;
		
		if(diff > sessionTime)
		{
			return null;
		}
		
		long userId = Long.parseLong(matcher.group(2));
		List<Object> roles = new ArrayList<>();
		String roleIds[] = matcher.group(3).split("\\,");
		
		for(String id : roleIds)
		{
			roles.add(idToRole.get(id));
		}
		
		UserDetails<Object> det = new UserDetails<Object>();
		det.setUserId(userId);
		det.setRoles(roles.toArray(new Object[0]));
		
		//when time is about to expire reset the response token
		if(diff >= (sessionTime - 1))
		{
			response.setHeader(ICommonConstants.HEADER_AUTHORIZATION_TOKEN, encrypt(userId, roles.toArray(new Object[0])));
		}
		
		return det;
	}
	
	/**
	 * Used to perform basic authentication
	 * @param token
	 * @return
	 */
	private String authenticate(HttpServletRequest request, String token)
	{
		String encodedUserPassword = token.replaceFirst("Basic ", "");
		
		try
		{
			byte[] decodedBytes = Base64.getDecoder().decode(encodedUserPassword);
			String usernameAndPassword = new String(decodedBytes, "UTF-8");
			
			StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
			String username = tokenizer.nextToken();
			String password = tokenizer.nextToken();
			
			UserDetails<?> user = authenticationService.authenticate(username, password);
			
			if(user == null)
			{
				return null;
			}
			
			request.setAttribute(IWebUtilsInternalConstants.REQ_ATTR_USER_DETAILS, user);
			return encrypt(user.getUserId(), user.getRoles());
		}catch(IOException ex)
		{
			logger.error("An error occurred while performing authentication using token - " + token, ex);
			return null;
		}
	}
	
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
			//check for authentication token
			String authenticationToken = request.getHeader(ICommonConstants.HEADER_AUTHENTICATION);

			if(StringUtils.isBlank(authenticationToken))
			{
				sendError(response, ICommonConstants.RESPONSE_CODE_AUTHENTICATION_ERROR, "Authentication failed. No authentication details provided");
				return false;
			}

			authorizationToken = authenticate(request, authenticationToken);
			
			if(authorizationToken == null)
			{
				sendError(response, ICommonConstants.RESPONSE_CODE_AUTHENTICATION_ERROR, "Authentication failed. Invalid authentication details provided");
				return false;
			}
			
			response.setHeader(ICommonConstants.HEADER_AUTHORIZATION_TOKEN, authorizationToken);
			return true;
		}

		
		//decrypt user details
		UserDetails<?> userDetails = decrypt(authorizationToken, response);
		
		if(userDetails == null)
		{
			sendError(response, ICommonConstants.RESPONSE_CODE_AUTHENTICATION_ERROR, "Authentication failed. Invalid authorization token specified");
			return false;
		}
		
		request.setAttribute(IWebUtilsInternalConstants.REQ_ATTR_USER_DETAILS, userDetails);
		return true;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
	{
		if(!checkAuthentication(request, response))
		{
			return false;
		}
		
		//TODO: Perform authorization here 
		return true;
	}

}
