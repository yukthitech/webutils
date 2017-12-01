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

package com.test.yukthi.webutils;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yukthitech.webutils.security.UserDetails;
import com.yukthitech.webutils.utils.WebUtils;

/**
 * User details for test
 * @author akiran
 */
public class TestUserDetails extends UserDetails
{
	/** The client id. */
	private long clientId;
	
	/** The roles. */
	private Set<SecurityRole> roles;
	
	/** The role ids. */
	private Set<Integer> roleIds;

	/**
	 * Instantiates a new test user details.
	 */
	public TestUserDetails()
	{}

	/**
	 * Instantiates a new test user details.
	 *
	 * @param userId
	 *            the user id
	 * @param roles
	 *            the roles
	 * @param clientId
	 *            the client id
	 */
	public TestUserDetails(long userId, Set<SecurityRole> roles, long clientId)
	{
		super(userId);
		this.clientId = clientId;
		setRoles(roles);
	}

	/**
	 * @param clientId
	 *            the {@link #clientId clientId} to set
	 */
	@JsonProperty("ci")
	public void setClientId(long clientId)
	{
		this.clientId = clientId;
	}

	/**
	 * @return the {@link #clientId clientId}
	 */
	public long getClientId()
	{
		return clientId;
	}

	/**
	 * Gets the roles.
	 *
	 * @return the roles
	 */
	@JsonIgnore
	public Set<SecurityRole> getRoles()
	{
		return roles;
	}

	/**
	 * Sets the roles.
	 *
	 * @param roles the new roles
	 */
	public void setRoles(Set<SecurityRole> roles)
	{
		this.roles = roles;
		this.roleIds = WebUtils.toEnumOrdinals(roles);
	}

	/**
	 * Gets the role ids.
	 *
	 * @return the role ids
	 */
	@JsonProperty("ro")
	public Set<Integer> getRoleIds()
	{
		return roleIds;
	}

	/**
	 * Sets the role ids.
	 *
	 * @param roleIds the new role ids
	 */
	public void setRoleIds(Set<Integer> roleIds)
	{
		this.roleIds = roleIds;
		this.roles = WebUtils.toEnums(roleIds, SecurityRole.class);
	}
}
