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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
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

	private ObjectMapper objectMapper = new ObjectMapper();
	
	
	/**
	 * Pattern for parsing tokens
	 */
	private Pattern TOKEN_PATTERN = Pattern.compile("(\\w+)\\-(.+)");
	
	/**
	 * Autowired webapp configuration
	 */
	@Autowired
	private WebutilsConfiguration configuration;
	
	/**
	 * Generates encrypted string with specified user details
	 * @param userDetails User details to be encrypted
	 * @return Encrypted string
	 */
	public String encrypt(UserDetails userDetails)
	{
		if(!configuration.isAuthEnabled())
		{
			return null;
		}
		
		//get current time in minutes
		long time = WebUtils.currentTimeInMin();
		userDetails.setTimeStamp(time);
		
		try
		{
			String userWithRoles = objectMapper.writeValueAsString(userDetails);
			String encryptedStr = time + "-" + CryptoUtils.encrypt(configuration.getSecretKey(), userWithRoles);
			userDetails.setAuthToken(encryptedStr);
			
			return encryptedStr;
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while encrypting user details - {}", userDetails);
		}
	}
	
	/**
	 * Converts the encrypted string into user details. If token is going to be expired in a min, new token
	 * gets generated and will be set in result user details.
	 * 
	 * @param encryptedString Encrypted user details string
	 * @return decrypted user details
	 * @throws SecurityException When invalid or expired token is specified.
	 */
	public UserDetails decrypt(String encryptedString) throws SecurityException
	{
		if(!configuration.isAuthEnabled())
		{
			return null;
		}
		
		String actualEncryptedString = encryptedString;
		
		//ensure the pattern is proper
		Matcher tokenMatcher = TOKEN_PATTERN.matcher(encryptedString);
		
		if(!tokenMatcher.matches())
		{
			logger.debug("Invalid auth token. No hyphen (-) found");
			throw new SecurityException("Invalid security token encountered");
		}
		
		String time = tokenMatcher.group(1);
		encryptedString = tokenMatcher.group(2);
		
		String decryptedStr = CryptoUtils.decrypt(configuration.getSecretKey(), encryptedString);
		UserDetails userDetails = null;
		
		try
		{
			userDetails = objectMapper.readValue(decryptedStr, configuration.getUserDetailsType());
		}catch(Exception ex)
		{
			logger.debug("Invalid auth token. Failed to deserialize user details.");
			throw new SecurityException("Invalid security token encountered");
		}
		
		//ensure time stamp is proper (which ensures token is not malformed)
		long reqTime = userDetails.getTimeStamp();
		
		if(!time.equals("" + reqTime))
		{
			logger.debug("Invalid auth token. Time stamp did not match");
			throw new SecurityException("Malformed security token encountered");
		}
		
		//get current time in minutes and ensure token is not time out
		long currentTime = System.currentTimeMillis() / 60000L;
		long diff = currentTime - reqTime;
		int sessionTimeoutTime = this.configuration.getSessionTimeOutInMin();
		int sessionExpiryTime = this.configuration.getSessionExpiryInMin();
		
		if(diff > sessionTimeoutTime)
		{
			logger.debug("Invalid auth token. Session timed out.");
			throw new SecurityException("Session timed out");
		}

		//if session expire time is specified
		if(sessionExpiryTime > 0)
		{
			//calculate the session duration
			long sessionDuration = currentTime - userDetails.getSessionStartTime();
			
			//if the session duration has crossed expiry time
			if(sessionDuration > sessionExpiryTime)
			{
				logger.debug("Invalid auth token. Session expired.");
				throw new SecurityException("Session expired");
			}
		}
		
		//when time is about to expire reset the response token
		if(diff >= (sessionTimeoutTime - 1))
		{
			userDetails.setAuthToken(encrypt(userDetails));
		}
		else
		{
			userDetails.setAuthToken(actualEncryptedString);
		}
		
		return userDetails;
	}

}
