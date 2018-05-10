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

package com.yukthitech.webutils.repository;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.yukthitech.persistence.annotations.DataType;
import com.yukthitech.persistence.annotations.DataTypeMapping;
import com.yukthitech.persistence.annotations.UniqueConstraint;
import com.yukthitech.persistence.annotations.UniqueConstraints;
import com.yukthitech.persistence.conversion.impl.JsonWithTypeConverter;

/**
 * Represents roles assigned to an user.
 * 
 * @author akiran
 */
@Table(name = "WEBUTILS_USER_ROLES")
@UniqueConstraints({
	@UniqueConstraint(name = "UQ_UROLE_ROLE", fields = {"user", "role", "ownerType", "ownerId"})
	})
public class UserRoleEntity extends WebutilsEntity
{
	/**
	 * User for which role is being added.
	 */
	@ManyToOne
	@Column(name = "USER_ID", nullable = false)
	private UserEntity user;
	
	/**
	 * Role being assigned.
	 */
	@DataTypeMapping(type = DataType.STRING, converterType = JsonWithTypeConverter.class)
	@Column(name = "ROLE", nullable = false, length = 200)
	private Object role;
	
	/**
	 * Owner type under which this role is being assigned. For global roles assign Object.class.getName()
	 */
	@Column(name = "OWNER_TYPE", nullable = false, length = 250)
	private String ownerType;
	
	/**
	 * Owner id under which this role is being assigned. For global roles this can be zero 
	 */
	@Column(name = "OWNER_ID", nullable = false)
	private Long ownerId = 0L;

	/**
	 * Gets the user for which role is being added.
	 *
	 * @return the user for which role is being added
	 */
	public UserEntity getUser()
	{
		return user;
	}

	/**
	 * Sets the user for which role is being added.
	 *
	 * @param user the new user for which role is being added
	 */
	public void setUser(UserEntity user)
	{
		this.user = user;
	}

	/**
	 * Gets the role being assigned.
	 *
	 * @return the role being assigned
	 */
	public Object getRole()
	{
		return role;
	}

	/**
	 * Sets the role being assigned.
	 *
	 * @param role the new role being assigned
	 */
	public void setRole(Object role)
	{
		this.role = role;
	}

	/**
	 * Gets the owner type under which this this role is being assigned. For global roles assign Object.class.getName().
	 *
	 * @return the owner type under which this this role is being assigned
	 */
	public String getOwnerType()
	{
		return ownerType;
	}

	/**
	 * Sets the owner type under which this this role is being assigned. For global roles assign Object.class.getName().
	 *
	 * @param ownerType the new owner type under which this this role is being assigned
	 */
	public void setOwnerType(String ownerType)
	{
		this.ownerType = ownerType;
	}

	/**
	 * Gets the owner id under which this role is being assigned. For global roles this can be zero.
	 *
	 * @return the owner id under which this role is being assigned
	 */
	public Long getOwnerId()
	{
		return ownerId;
	}

	/**
	 * Sets the owner id under which this role is being assigned. For global roles this can be zero.
	 *
	 * @param ownerId the new owner id under which this role is being assigned
	 */
	public void setOwnerId(Long ownerId)
	{
		this.ownerId = ownerId;
	}
}
