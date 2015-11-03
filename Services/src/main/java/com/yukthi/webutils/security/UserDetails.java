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

/**
 * @author akiran
 *
 */
public class UserDetails<T>
{
	/**
	 * Unique user id
	 */
	private long userId;
	
	/**
	 * User roles
	 */
	private T roles[];

	public UserDetails()
	{}
	
	public UserDetails(long userId, T[] roles)
	{
		this.userId = userId;
		this.roles = roles;
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
	 * @param userId the new unique user id
	 */
	public void setUserId(long userId)
	{
		this.userId = userId;
	}

	/**
	 * Gets the user roles.
	 *
	 * @return the user roles
	 */
	public T[] getRoles()
	{
		return roles;
	}

	/**
	 * Sets the user roles.
	 *
	 * @param roles the new user roles
	 */
	public void setRoles(T[] roles)
	{
		this.roles = roles;
	}
}
