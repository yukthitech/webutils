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
package com.yukthi.webutils.repository;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.yukthi.persistence.annotations.DataType;
import com.yukthi.persistence.annotations.DataTypeMapping;
import com.yukthi.persistence.annotations.UniqueConstraint;
import com.yukthi.persistence.annotations.UniqueConstraints;
import com.yukthi.persistence.conversion.impl.JsonConverter;

/**
 * Represents roles assigned to an user
 * 
 * @author akiran
 */
@Table(name = "USER_ROLES")
@UniqueConstraints({
	@UniqueConstraint(name = "UQ_UROLE_ROLE", fields = {"user", "role", "ownerType", "ownerId"})
})
public class UserRoleEntity implements ITrackedEntity
{
	/**
	 * Primary key
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	/** The version. */
	@Version
	@Column(name = "VERSION", nullable = false)
	private Integer version = 1;

	/**
	 * User for which role is being added
	 */
	@ManyToOne
	@Column(name = "USER_ID", nullable = false)
	private UserEntity user;
	
	/**
	 * Role being assigned
	 */
	@DataTypeMapping(type = DataType.STRING, converterType = JsonConverter.class)
	@Column(name = "ROLE", nullable = false, length = 500)
	private Object role;
	
	/**
	 * Owner type under which this this role is being assigned. For global roles assign Object.class.getName()
	 */
	@Column(name = "OWNER_TYPE", nullable = false)
	private String ownerType;
	
	/**
	 * Owner id under which this role is being assigned. For global roles this can be zero 
	 */
	@Column(name = "OWNER_ID", nullable = false)
	private Long ownerId = 0L;
	
	/**
	 * Created by user
	 */
	@ManyToOne
	@Column(name = "CREATED_BY_ID")
	private UserEntity createdBy;
	
	/**
	 * Created on time
	 */
	@Column(name = "CREATED_ON")
	@DataTypeMapping(type = DataType.DATE_TIME)
	private Date createdOn;

	/**
	 * Updating user
	 */
	@ManyToOne
	@Column(name = "UPDATED_BY_ID")
	private UserEntity updatedBy;
	
	/**
	 * Updated on
	 */
	@Column(name = "UPDATED_ON")
	@DataTypeMapping(type = DataType.DATE_TIME)
	private Date updatedOn;

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.IEntity#getId()
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * Sets the primary key.
	 *
	 * @param id the new primary key
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

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

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.IEntity#getVersion()
	 */
	@Override
	public Integer getVersion()
	{
		return version;
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.IEntity#setVersion(java.lang.Integer)
	 */
	@Override
	public void setVersion(Integer version)
	{
		this.version = version;
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.repository.ITrackedEntity#getCreatedBy()
	 */
	public UserEntity getCreatedBy()
	{
		return createdBy;
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.repository.ITrackedEntity#setCreatedBy(com.yukthi.webutils.repository.UserEntity)
	 */
	public void setCreatedBy(UserEntity createdBy)
	{
		this.createdBy = createdBy;
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.repository.ITrackedEntity#getCreatedOn()
	 */
	public Date getCreatedOn()
	{
		return createdOn;
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.repository.ITrackedEntity#setCreatedOn(java.util.Date)
	 */
	public void setCreatedOn(Date createdOn)
	{
		this.createdOn = createdOn;
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.repository.ITrackedEntity#getUpdatedBy()
	 */
	public UserEntity getUpdatedBy()
	{
		return updatedBy;
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.repository.ITrackedEntity#setUpdatedBy(com.yukthi.webutils.repository.UserEntity)
	 */
	public void setUpdatedBy(UserEntity updatedBy)
	{
		this.updatedBy = updatedBy;
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.repository.ITrackedEntity#getUpdatedOn()
	 */
	public Date getUpdatedOn()
	{
		return updatedOn;
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.repository.ITrackedEntity#setUpdatedOn(java.util.Date)
	 */
	public void setUpdatedOn(Date updatedOn)
	{
		this.updatedOn = updatedOn;
	}
	
	
}
