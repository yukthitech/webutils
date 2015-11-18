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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yukthi.utils.CryptoUtils;
import com.yukthi.utils.exceptions.InvalidStateException;
import com.yukthi.webutils.WebutilsConfiguration;
import com.yukthi.webutils.utils.WebUtils;

/**
 * Service to enrypt and decrypt auth tokens
 * @author akiran
 */
@Component
public class SecurityEncryptionService
{
	private static Logger logger = LogManager.getLogger(SecurityEncryptionService.class);
	
	private static final String KEY_TIME = "tm";
	private static final String KEY_START_TIME = "stm";
	private static final String KEY_USER_ID = "ui";
	private static final String KEY_USER_ROLES = "ur";
	private static final String KEY_SECURITY_FIELDS = "sf";
	
	/**
	 * Pattern for parsing tokens
	 */
	private Pattern TOKEN_PATTERN = Pattern.compile("(\\w+)\\-(.*)");
	
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
	 * Encodes the given map into randomized string in plain text
	 * @param map Map to be encoded
	 * @return Random encoded string
	 */
	private String encodeTokens(Map<String, String> map)
	{
		StringBuilder builder = new StringBuilder();
		
		//shuffle the map entries so that end result is always random
		List<Map.Entry<String, String>> entries = new ArrayList<>(map.entrySet());
		Collections.shuffle(entries);
		
		//loop through entries and add to res
		for(Map.Entry<String, String> entry : entries)
		{
			builder.append(entry.getKey()).append("-").append(entry.getValue());
			builder.append(":");
		}
		
		//delete trailing :
		builder.deleteCharAt(builder.length() - 1);
		
		return builder.toString();
	}
	
	/**
	 * Decodes the given string into map. String should have been encoded by {@link #encodeTokens(Map)} method
	 * @param str String to be decoded
	 * @return Decoded map
	 */
	private Map<String, String> decodeTokens(String str)
	{
		//split string into tokens
		String tokens[] = str.split("\\:");
		Map<String, String> map = new HashMap<>();
		Matcher matcher = null;
		
		//loop through tokens and build map
		for(String token : tokens)
		{
			matcher = TOKEN_PATTERN.matcher(token);
			
			if(!matcher.matches())
			{
				logger.error("Invalid token encountered - {}", token);
				throw new InvalidStateException("Invalid token encountered");
			}
			
			map.put(matcher.group(1).trim(), matcher.group(2).trim());
		}
		
		return map;
	}

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
		long time = WebUtils.currentTimeInMin();
		
		Map<String, String> tokenMap = new HashMap<>();
		
		//add current time stamp, user-id and roles
		tokenMap.put(KEY_TIME, "" + time);
		tokenMap.put(KEY_USER_ID, "" + userDetails.getUserId());
		tokenMap.put(KEY_START_TIME, "" + userDetails.getSessionStartTime());

		StringBuilder rolesStr = new StringBuilder();
		
		//add user roles to builder
		for(Object role : userDetails.getRoles())
		{
			//if invalid role is specified
			if(!this.roleToId.containsKey(role))
			{
				throw new InvalidStateException("Invalid role encountered - {}. Expected roles - {}", role, this.roleToId.keySet());
			}
			
			rolesStr.append(this.roleToId.get(role)).append(",");
		}
		
		//delete trailing comma
		rolesStr.deleteCharAt(rolesStr.length() - 1);
		
		tokenMap.put(KEY_USER_ROLES, rolesStr.toString());
		
		//set security fields
		String secFieldValues[] = this.userDetailsWrapper.getSecurityFields(userDetails);
		StringBuilder secFieldsStr = new StringBuilder();
		
		if(secFieldValues.length > 0)
		{
			for(String value : secFieldValues)
			{
				secFieldsStr.append(value).append(",");
			}

			//delete trailing comma
			secFieldsStr.deleteCharAt(secFieldsStr.length() - 1);
		}
		
		tokenMap.put(KEY_SECURITY_FIELDS, secFieldsStr.toString());
		
		//encrypt built string 
		String userWithRoles = encodeTokens(tokenMap);
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
		
		String actualEncryptedString = encryptedString;
		
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
		Map<String, String> tokenMap = decodeTokens(decryptedStr);
		
		//extract and ensure time stamps are proper (which ensures token is not malformed)
		String timeStamp = tokenMap.get(KEY_TIME);
		String sessionStartTimeStr = tokenMap.get(KEY_START_TIME);
		
		if(timeStamp == null || !timeStamp.equals(time))
		{
			logger.debug("Invalid auth token. Time stamp did not match");
			throw new SecurityException("Malformed security token encountered");
		}
		
		if(StringUtils.isBlank(sessionStartTimeStr))
		{
			logger.debug("Invalid auth token. Session start time missing.");
			throw new SecurityException("Malformed security token encountered");
		}

		//get current time in minutes and ensure token is not time out
		long currentTime = System.currentTimeMillis() / 60000L;
		long reqTime = Long.parseLong(timeStamp);
		long diff = currentTime - reqTime;
		int sessionTimeoutTime = this.configuration.getSessionTimeOutInMin();
		int sessionExpiryTime = this.configuration.getSessionExpiryInMin();
		
		if(diff > sessionTimeoutTime)
		{
			logger.debug("Invalid auth token. Session timed out.");
			throw new SecurityException("Session timed out");
		}

		long sessionStartTime = Long.parseLong(sessionStartTimeStr);
		
		//if session expire time is specified
		if(sessionExpiryTime > 0)
		{
			//calculate the session duration
			long sessionDuration = currentTime - sessionStartTime;
			
			//if the session duration has crossed expiry time
			if(sessionDuration > sessionExpiryTime)
			{
				logger.debug("Invalid auth token. Session expired.");
				throw new SecurityException("Session expired");
			}
		}
		
		//fetch user id and roles 
		long userId = Long.parseLong(tokenMap.get(KEY_USER_ID));
		Set<Object> roles = new HashSet<>();
		String roleIds[] = tokenMap.get(KEY_USER_ROLES).split("\\,");
		
		for(String id : roleIds)
		{
			roles.add(idToRole.get(id));
		}
		
		//extract security fields
		String values[] = tokenMap.get(KEY_SECURITY_FIELDS).split("\\,");
		
		//build the user details object
		UserDetails<Object> det = (UserDetails)userDetailsWrapper.newDetails();
		det.setUserId(userId);
		det.setRoles(roles);
		det.setSessionStartTime(sessionStartTime);
		userDetailsWrapper.setSecurityFields(det, values);
		
		//when time is about to expire reset the response token
		if(diff >= (sessionTimeoutTime - 1))
		{
			det.setAuthToken(encrypt(det));
		}
		else
		{
			det.setAuthToken(actualEncryptedString);
		}
		
		return det;
	}

}
