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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * User details representing current user, which is used by security services.
 * 
 * @author akiran
 */
public class UserDetails
{
	/**
	 * Unique user id.
	 */
	private long userId;
	
	/**
	 * Type of the user.
	 */
	private String userType;

	/**
	 * Encrypted authentication/authorization token.
	 */
	private String authToken;
	
	/**
	 * Instantiates a new user details.
	 */
	public UserDetails()
	{}

	/**
	 * Instantiates a new user details.
	 *
	 * @param userId
	 *            the user id
	 */
	public UserDetails(long userId)
	{
		this.userId = userId;
	}

	/**
	 * Gets the unique user id.
	 *
	 * @return the unique user id
	 */
	@JsonProperty("u")
	public long getUserId()
	{
		return userId;
	}

	/**
	 * Sets the unique user id.
	 *
	 * @param userId
	 *            the new unique user id
	 */
	public void setUserId(long userId)
	{
		this.userId = userId;
	}

	/**
	 * Gets the encrypted authentication/authorization token.
	 *
	 * @return the encrypted authentication/authorization token
	 */
	@JsonIgnore
	public String getAuthToken()
	{
		return authToken;
	}

	/**
	 * Sets the encrypted authentication/authorization token.
	 *
	 * @param authToken
	 *            the new encrypted authentication/authorization token
	 */
	public void setAuthToken(String authToken)
	{
		this.authToken = authToken;
	}

	/**
	 * Gets the type of the user.
	 *
	 * @return the type of the user
	 */
	public String getUserType()
	{
		return userType;
	}

	/**
	 * Sets the type of the user.
	 *
	 * @param userType the new type of the user
	 */
	public void setUserType(String userType)
	{
		this.userType = userType;
	}
}
