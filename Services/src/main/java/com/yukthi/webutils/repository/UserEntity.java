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
import com.yukthi.persistence.conversion.impl.PasswordEncryptionConverter;
import com.yukthi.webutils.repository.ITrackedEntity;

/**
 * User details for authentication. All entities like Customer, Client or admin should create
 * an entry into this table which needs authentication.
 * 
 * @author akiran
 */
@Table(name = "USERS")
@UniqueConstraints({
	@UniqueConstraint(name = "UQ_USERS_OWNR_UNAME", fields = {"ownerType", "ownerId", "userName"})
})
public class UserEntity implements ITrackedEntity
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
	private Integer version;

	/**
	 * User name used for authentication. This field value will be null for deleted users
	 */
	@Column(name = "USER_NAME", length = 50, nullable = true)
	private String userName;

	/**
	 * Password used for authentication
	 */
	@Column(name = "PASSWORD", length = 500, nullable = false)
	@DataTypeMapping(converterType = PasswordEncryptionConverter.class)
	private String password;
	
	/**
	 * Display name
	 */
	@Column(name = "DISPLAY_NAME", length = 500, nullable = false)
	private String displayName;

	/**
	 * Owner under which this user is being defined. Idea is under different owners (customers)
	 * same user name can be used. Zero value is used for Administrator 
	 */
	@Column(name = "OWNER_TYPE", nullable = false)
	private String ownerType;
	
	/**
	 * Owner under which this user is being defined. Idea is under different owners (customers)
	 * same user name can be used. Zero value is used for Administrator 
	 */
	@Column(name = "OWNER_ID", nullable = false)
	private Long ownerId = 0L;

	/**
	 * Actual entity for which this user object is created
	 */
	@Column(name = "BASE_ENT_TYPE", length = 200)
	private String  baseEntityType;
	
	/**
	 * Actual entity id, for which this auth details is created
	 */
	@Column(name = "BASE_ENT_ID")
	private Long baseEntityId;
	
	/**
	 * Flag indicating if this user is deleting
	 */
	@Column(name = "DELETED")
	private boolean deleted = false;
	
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
	
	/**
	 * Instantiates a new user entity.
	 */
	public UserEntity()
	{}
	
	/**
	 * Instantiates a new user entity.
	 *
	 * @param id the id
	 */
	public UserEntity(Long id)
	{
		this.id = id;
	}

	/**
	 * Gets the primary key.
	 *
	 * @return the primary key
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
	 * Gets the user name used for authentication.
	 *
	 * @return the user name used for authentication
	 */
	public String getUserName()
	{
		return userName;
	}

	/**
	 * Sets the user name used for authentication.
	 *
	 * @param userName the new user name used for authentication
	 */
	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	/**
	 * Gets the password used for authentication.
	 *
	 * @return the password used for authentication
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * Sets the password used for authentication.
	 *
	 * @param password the new password used for authentication
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}
	

	/**
	 * Gets the owner under which this user is being defined. Idea is under different owners (customers) same user name can be used. Zero value is used for Administrator.
	 *
	 * @return the owner under which this user is being defined
	 */
	public String getOwnerType()
	{
		return ownerType;
	}

	/**
	 * Sets the owner under which this user is being defined. Idea is under different owners (customers) same user name can be used. Zero value is used for Administrator.
	 *
	 * @param ownerType the new owner under which this user is being defined
	 */
	public void setOwnerType(String ownerType)
	{
		this.ownerType = ownerType;
	}

	/**
	 * Gets the owner under which this user is being defined. Idea is under different owners (customers) same user name can be used.
	 *
	 * @return the owner under which this user is being defined
	 */
	public Long getOwnerId()
	{
		return ownerId;
	}

	/**
	 * Sets the owner under which this user is being defined. Idea is under different owners (customers) same user name can be used.
	 *
	 * @param ownerId the new owner under which this user is being defined
	 */
	public void setOwnerId(Long ownerId)
	{
		this.ownerId = ownerId;
	}

	/**
	 * Gets the actual entity for which this user object is created.
	 *
	 * @return the actual entity for which this user object is created
	 */
	public String getBaseEntityType()
	{
		return baseEntityType;
	}

	/**
	 * Sets the actual entity for which this user object is created.
	 *
	 * @param baseEntityType the new actual entity for which this user object is created
	 */
	public void setBaseEntityType(String baseEntityType)
	{
		this.baseEntityType = baseEntityType;
	}

	/**
	 * Gets the actual entity id, for which this auth details is created.
	 *
	 * @return the actual entity id, for which this auth details is created
	 */
	public Long getBaseEntityId()
	{
		return baseEntityId;
	}

	/**
	 * Sets the actual entity id, for which this auth details is created.
	 *
	 * @param baseEntityId the new actual entity id, for which this auth details is created
	 */
	public void setBaseEntityId(Long baseEntityId)
	{
		this.baseEntityId = baseEntityId;
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.IEntity#getVersion()
	 */
	public Integer getVersion()
	{
		return version;
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.IEntity#setVersion(java.lang.Integer)
	 */
	public void setVersion(Integer version)
	{
		this.version = version;
	}

	/**
	 * Gets the display name.
	 *
	 * @return the display name
	 */
	public String getDisplayName()
	{
		return displayName;
	}

	/**
	 * Sets the display name.
	 *
	 * @param displayName the new display name
	 */
	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	/**
	 * Checks if is flag indicating if this user is deleting.
	 *
	 * @return the flag indicating if this user is deleting
	 */
	public boolean isDeleted()
	{
		return deleted;
	}

	/**
	 * Sets the flag indicating if this user is deleting.
	 *
	 * @param deleted the new flag indicating if this user is deleting
	 */
	public void setDeleted(boolean deleted)
	{
		this.deleted = deleted;
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
