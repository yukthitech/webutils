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

package com.yukthitech.webutils;

import com.yukthitech.webutils.user.UserRoleService;

/**
 * Key used to organize user roles by {@link UserRoleService}
 * 
 * @author akiran
 */
public class UserRoleKey
{
	/**
	 * Owner of the role
	 */
	private String ownerType = Object.class.getName();

	/**
	 * Owner id
	 */
	private long ownerId = 0L;

	/**
	 * User role
	 */
	private Object role;

	/**
	 * Instantiates a new user role key.
	 */
	public UserRoleKey()
	{}

	/**
	 * Instantiates a new user role key.
	 *
	 * @param role the role
	 */
	public UserRoleKey(Object role)
	{
		this.role = role;
	}

	/**
	 * Instantiates a new user role key.
	 *
	 * @param ownerType the owner type
	 * @param ownerId the owner id
	 * @param role the role
	 */
	public UserRoleKey(String ownerType, long ownerId, Object role)
	{
		this.ownerType = ownerType;
		this.ownerId = ownerId;
		this.role = role;
	}

	/**
	 * Gets the owner of the role.
	 *
	 * @return the owner of the role
	 */
	public String getOwnerType()
	{
		return ownerType;
	}

	/**
	 * Sets the owner of the role.
	 *
	 * @param ownerType the new owner of the role
	 */
	public void setOwnerType(String ownerType)
	{
		this.ownerType = ownerType;
	}

	/**
	 * Gets the owner id.
	 *
	 * @return the owner id
	 */
	public long getOwnerId()
	{
		return ownerId;
	}

	/**
	 * Sets the owner id.
	 *
	 * @param ownerId the new owner id
	 */
	public void setOwnerId(long ownerId)
	{
		this.ownerId = ownerId;
	}

	/**
	 * Gets the user role.
	 *
	 * @return the user role
	 */
	public Object getRole()
	{
		return role;
	}

	/**
	 * Sets the user role.
	 *
	 * @param role the new user role
	 */
	public UserRoleKey setRole(Object role)
	{
		this.role = role;
		return this;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(obj == this)
		{
			return true;
		}

		if(!(obj instanceof UserRoleKey))
		{
			return false;
		}

		UserRoleKey other = (UserRoleKey) obj;
		
		if(ownerType != null && !ownerType.equals(other.ownerType))
		{
			return false;
		}

		if(role != null && !role.equals(other.role))
		{
			return false;
		}

		return (ownerId == other.ownerId);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashcode()
	 */
	@Override
	public int hashCode()
	{
		return (ownerType != null ? ownerType.hashCode() : 0) 
				+ (role != null ? role.hashCode() : 0) 
				+ (int)ownerId;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Role: ").append(role);
		builder.append(",").append("Owner: ").append(ownerType).append(" - ").append(ownerId);

		builder.append("]");
		return builder.toString();
	}

}
