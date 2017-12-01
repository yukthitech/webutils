/*
 * The MIT License (MIT)
 * Copyright (c) 2016 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

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

package com.yukthitech.webutils.common.models;

import java.util.Set;

/**
 * Details and configuration of the current user. This beans contains all the information about current user needed by client.
 * 
 * @author akiran
 */
public class ActiveUserModel
{
	
	/**
	 * Current user id.
	 */
	private long userId;

	/**
	 * Display name of current user.
	 */
	private String displayName;

	/**
	 * Preferred date format of the user.
	 */
	private String jsDateFormat;

	/**
	 * High level roles of the user.
	 */
	private Set<? extends Enum<?>> roles;
	
	/**
	 * Gets the current user id.
	 *
	 * @return the current user id
	 */
	public long getUserId()
	{
		return userId;
	}

	/**
	 * Sets the current user id.
	 *
	 * @param userId
	 *            the new current user id
	 */
	public void setUserId(long userId)
	{
		this.userId = userId;
	}

	/**
	 * Gets the display name of current user.
	 *
	 * @return the display name of current user
	 */
	public String getDisplayName()
	{
		return displayName;
	}

	/**
	 * Sets the display name of current user.
	 *
	 * @param displayName
	 *            the new display name of current user
	 */
	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	/**
	 * Gets the preferred date format of the user.
	 *
	 * @return the preferred date format of the user
	 */
	public String getJsDateFormat()
	{
		return jsDateFormat;
	}

	/**
	 * Sets the preferred date format of the user.
	 *
	 * @param jsDateFormat
	 *            the new preferred date format of the user
	 */
	public void setJsDateFormat(String jsDateFormat)
	{
		this.jsDateFormat = jsDateFormat;
	}

	/**
	 * Gets the high level roles of the user.
	 *
	 * @return the high level roles of the user
	 */
	public Set<? extends Enum<?>> getRoles()
	{
		return roles;
	}

	/**
	 * Sets the high level roles of the user.
	 *
	 * @param roles
	 *            the new high level roles of the user
	 */
	public void setRoles(Set<? extends Enum<?>> roles)
	{
		this.roles = roles;
	}
}
