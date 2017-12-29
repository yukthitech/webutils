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

package com.yukthitech.webutils.common;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * User details representing current user, which is used by security services.
 * 
 * @author akiran
 * @param <R> Type of roles used by application.
 */
public class UserDetails<R extends Enum<R>>
{
	/**
	 * Unique user id.
	 */
	private long userId;
	
	/**
	 * Display name of the user.
	 */
	private String displayName;
	
	/**
	 * Encrypted authentication/authorization token.
	 */
	private String authToken;
	
	/**
	 * Roles of the user.
	 */
	private Set<R> roles;
	
	/**
	 * Date format to be used in JS.
	 */
	private String jsDateFormat;
	
	/**
	 * Instantiates a new user details.
	 */
	public UserDetails()
	{}

	/**
	 * Instantiates a new user details.
	 *
	 * @param userId the user id
	 * @param displayName the display name
	 * @param dateFormat date format to be used.
	 */
	public UserDetails(long userId, String displayName, String dateFormat)
	{
		this.userId = userId;
		this.displayName = displayName;
		this.jsDateFormat = dateFormat;
	}

	/**
	 * Gets the unique user id.
	 *
	 * @return the unique user id
	 */
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
	 * Gets the display name of the user.
	 *
	 * @return the display name of the user
	 */
	public String getDisplayName()
	{
		return displayName;
	}

	/**
	 * Sets the display name of the user.
	 *
	 * @param displayName the new display name of the user
	 */
	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	/**
	 * Gets the roles of the user.
	 *
	 * @return the roles of the user
	 */
	public Set<R> getRoles()
	{
		return roles;
	}

	/**
	 * Sets the roles of the user.
	 *
	 * @param roles the new roles of the user
	 */
	public void setRoles(Set<R> roles)
	{
		this.roles = roles;
	}
	
	/**
	 * Checkes if this user has specified role.
	 * @param role role to check.
	 * @return true if role is present.
	 */
	public boolean hasRole(R role)
	{
		return (roles != null && roles.contains(role));
	}

	/**
	 * Gets the date format to be used in JS.
	 *
	 * @return the date format to be used in JS
	 */
	public String getJsDateFormat()
	{
		return jsDateFormat;
	}

	/**
	 * Sets the date format to be used in JS.
	 *
	 * @param dateFormat the new date format to be used in JS
	 */
	public void setJsDateFormat(String dateFormat)
	{
		this.jsDateFormat = dateFormat;
	}
}
