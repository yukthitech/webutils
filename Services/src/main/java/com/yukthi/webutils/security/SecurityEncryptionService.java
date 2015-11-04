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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yukthi.utils.CryptoUtils;
import com.yukthi.utils.exceptions.InvalidStateException;
import com.yukthi.webutils.WebutilsConfiguration;

/**
 * Service to enrypt and decrypt auth tokens
 * @author akiran
 */
@Component
public class SecurityEncryptionService
{
	private static Logger logger = LogManager.getLogger(SecurityEncryptionService.class);
	
	/**
	 * Expected encrypted string pattern
	 */
	private Pattern ENC_STR_PATTERN = Pattern.compile("(\\d+)\\:(\\d+)\\:([\\d\\,]+)\\:([\\w\\.\\,]*)");
	
	/**
	 * Autowired webapp configuration
	 */
	@Autowired
	private WebutilsConfiguration configuration;
	
	/**
	 * Roles to ordinal map
	 */
	private Map<Object, Integer> roleToId = new HashMap<>();
	
	/**
	 * Ordinal to role map
	 */
	private Map<String, Object> idToRole = new HashMap<>();
	
	/**
	 * Wrapper to set or get security fields from user details
	 */
	private UserDetailsWrapper userDetailsWrapper;

	/**
	 * Post construct method while loads all the roles and user details wrapper
	 */
	@PostConstruct
	public void init()
	{
		
		if(!configuration.isEnableAuth())
		{
			return;
		}
		
		Class<? extends Enum<?>> roleType = configuration.getRolesEnumType();
		Enum<?> enumValues[] = roleType.getEnumConstants();
		
		for(Enum<?> e : enumValues)
		{
			roleToId.put(e, e.ordinal());
			idToRole.put("" + e.ordinal(), e);
		}
		
		userDetailsWrapper = new UserDetailsWrapper(configuration.getUserDetailsType());
	}
	
	/**
	 * Generates encrypted string with specified user if and roles
	 * @param userDetails User details to be encrypted
	 * @return Encrypted string
	 */
	public String encrypt(UserDetails<?> userDetails)
	{
		if(!configuration.isEnableAuth())
		{
			return null;
		}
		
		if(userDetails.getRoles() == null)
		{
			throw new InvalidStateException("No roles found for user - {}", userDetails.getUserId());
		}
		
		//get current time in minutes
		long time = System.currentTimeMillis() / 60000L;
		
		StringBuilder builder = new StringBuilder();
		
		//add current time stamp, user-id and roles
		builder.append(time).append(":");
		builder.append(userDetails.getUserId()).append(":");
		
		//add user roles to builder
		for(Object role : userDetails.getRoles())
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
		
		//set security fields
		builder.append(":");
		
		String secFieldValues[] = this.userDetailsWrapper.getSecurityFields(userDetails);
		
		if(secFieldValues.length > 0)
		{
			for(String value : secFieldValues)
			{
				builder.append(value).append(",");
			}

			//delete trailing comma
			builder.deleteCharAt(builder.length() - 1);
		}
		
		//encrypt built string 
		String userWithRoles = builder.toString();
		String encryptedStr = time + "-" + CryptoUtils.encrypt(configuration.getSecretKey(), userWithRoles);
		
		userDetails.setAuthToken(encryptedStr);
		return encryptedStr;
	}
	
	/**
	 * Converts the encrypted string into user details. If token is going to be expired in a min, new token
	 * gets generated and will be set in result user details.
	 * 
	 * @param encryptedString Encrypted user details string
	 * @return decrypted user details string
	 * @throws SecurityException When invalid or expired token is specified.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public UserDetails<?> decrypt(String encryptedString) throws SecurityException
	{
		if(!configuration.isEnableAuth())
		{
			return null;
		}
		
		//ensure the pattern is proper
		int idx = encryptedString.indexOf("-");
		
		if(idx <= 0)
		{
			logger.debug("Invalid auth token. No hyphen (-) found");
			throw new SecurityException("Invalid security token encountered");
		}
		
		String time = encryptedString.substring(0, idx);
		encryptedString = encryptedString.substring(idx + 1);
		
		String decryptedStr = CryptoUtils.decrypt(configuration.getSecretKey(), encryptedString);
		Matcher matcher = ENC_STR_PATTERN.matcher(decryptedStr);
		
		if(!matcher.matches())
		{
			logger.debug("Invalid auth token. Invalid pattern");
			throw new SecurityException("Invalid security token encountered");
		}
		
		//extract and ensure time stamps are proper (which ensures token is not malformed)
		String time1 = matcher.group(1);
		
		if(!time1.equals(time))
		{
			logger.debug("Invalid auth token. Time stamp did not match");
			throw new SecurityException("Malformed security token encountered");
		}
		
		//get current time in minutes and ensure token is not time out
		long currentTime = System.currentTimeMillis() / 60000L;
		long reqTime = Long.parseLong(time1);
		long diff = currentTime - reqTime;
		int sessionTime = this.configuration.getSessionTimeOutInMin();
		
		if(diff > sessionTime)
		{
			logger.debug("Invalid auth token. Session timed out.");
			throw new SecurityException("Session timeout");
		}
		
		//fetch user id and roles 
		long userId = Long.parseLong(matcher.group(2));
		Set<Object> roles = new HashSet<>();
		String roleIds[] = matcher.group(3).split("\\,");
		
		for(String id : roleIds)
		{
			roles.add(idToRole.get(id));
		}
		
		//extract security fields
		String values[] = matcher.group(4).split("\\,");
		
		//build the user details object
		UserDetails<Object> det = (UserDetails)userDetailsWrapper.newDetails();
		det.setUserId(userId);
		det.setRoles(roles);
		userDetailsWrapper.setSecurityFields(det, values);
		
		//when time is about to expire reset the response token
		if(diff >= (sessionTime - 1))
		{
			det.setAuthToken(encrypt(det));
		}
		else
		{
			det.setAuthToken(encryptedString);
		}
		
		return det;
	}

}
