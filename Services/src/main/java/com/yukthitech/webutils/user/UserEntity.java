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

package com.yukthitech.webutils.user;

import javax.persistence.Column;
import javax.persistence.Table;

import com.yukthitech.persistence.annotations.DataTypeMapping;
import com.yukthitech.persistence.annotations.NotUpdateable;
import com.yukthitech.persistence.annotations.UniqueConstraint;
import com.yukthitech.persistence.annotations.UniqueConstraints;
import com.yukthitech.persistence.conversion.impl.PasswordEncryptionConverter;
import com.yukthitech.webutils.repository.WebutilsEntity;

/**
 * User details for authentication. All entities like Customer, Client or admin should create
 * an entry into this table which needs authentication.
 * 
 * @author akiran
 */
@Table(name = "WEBUTILS_USERS")
@UniqueConstraints({
	@UniqueConstraint(name = "UQ_USERS_OWNR_UNAME", fields = {"spaceIdentity", "userName"}, finalName = false),
	@UniqueConstraint(name = "UQ_USERS_BASE_DET", fields = {"baseEntityType", "baseEntityId"}, finalName = false)
	})
public class UserEntity extends WebutilsEntity
{
	/**
	 * User name used for authentication. This field value will be null for deleted users
	 */
	@Column(name = "USER_NAME", length = 50, nullable = true)
	private String userName;

	/**
	 * Password used for authentication.
	 */
	@Column(name = "PASSWORD", length = 500, nullable = false)
	@DataTypeMapping(converterType = PasswordEncryptionConverter.class)
	private String password;
	
	/**
	 * Password set temporarily during reset process.
	 */
	@Column(name = "RESET_PASSWORD", length = 500, nullable = true)
	@DataTypeMapping(converterType = PasswordEncryptionConverter.class)
	private String resetPassword;

	/**
	 * Display name.
	 */
	@Column(name = "DISPLAY_NAME", length = 500, nullable = false)
	private String displayName;

	/**
	 * Actual entity for which this user object is created.
	 */
	@NotUpdateable
	@Column(name = "BASE_ENT_TYPE", length = 200)
	private String  baseEntityType;
	
	/**
	 * Actual entity id, for which this auth details is created.
	 */
	@NotUpdateable
	@Column(name = "BASE_ENT_ID")
	private Long baseEntityId;
	
	/**
	 * Owner entity type under which this user is getting created.
	 */
	@NotUpdateable
	@Column(name = "OWNER_ENT_TYPE", length = 200)
	private String  ownerEntityType;
	
	/**
	 * Owner entity id under which this user is getting created.
	 */
	@NotUpdateable
	@Column(name = "OWNER_ENT_ID")
	private Long ownerEntityId;

	/**
	 * App specific custom data at user level.
	 */
	@Column(name = "CUSTOM_DATA", length = 2000)
	private String customData;

	/**
	 * Flag indicating if this user is deleting.
	 */
	@Column(name = "DELETED")
	private boolean deleted = false;
	
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
		super(id);
	}
	
	/**
	 * Instantiates a new user entity.
	 *
	 * @param userName the user name
	 * @param password the password
	 * @param displayName the display name
	 */
	public UserEntity(String userName, String password, String displayName)
	{
		this.userName = userName;
		this.password = password;
		this.displayName = displayName;
	}

	/**
	 * Instantiates a new user entity.
	 *
	 * @param spaceIdentity the space identity
	 * @param userName user name used for authentication. This field value will be null for deleted users
	 * @param password password used for authentication.
	 * @param displayName display name.
	 */
	public UserEntity(String spaceIdentity, String userName, String password, String displayName)
	{
		this.userName = userName;
		this.password = password;
		this.displayName = displayName;
		
		super.setSpaceIdentity(spaceIdentity);
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
	 * Gets the password set temporarily during reset process.
	 *
	 * @return the password set temporarily during reset process
	 */
	public String getResetPassword() 
	{
		return resetPassword;
	}

	/**
	 * Sets the password set temporarily during reset process.
	 *
	 * @param resetPassword the new password set temporarily during reset process
	 */
	public void setResetPassword(String resetPassword) 
	{
		this.resetPassword = resetPassword;
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

	/**
	 * Gets the owner entity type under which this user is getting created.
	 *
	 * @return the owner entity type under which this user is getting created
	 */
	public String getOwnerEntityType()
	{
		return ownerEntityType;
	}

	/**
	 * Sets the owner entity type under which this user is getting created.
	 *
	 * @param ownerEntityType the new owner entity type under which this user is getting created
	 */
	public void setOwnerEntityType(String ownerEntityType)
	{
		this.ownerEntityType = ownerEntityType;
	}

	/**
	 * Gets the owner entity id under which this user is getting created.
	 *
	 * @return the owner entity id under which this user is getting created
	 */
	public Long getOwnerEntityId()
	{
		return ownerEntityId;
	}

	/**
	 * Sets the owner entity id under which this user is getting created.
	 *
	 * @param ownerEntityId the new owner entity id under which this user is getting created
	 */
	public void setOwnerEntityId(Long ownerEntityId)
	{
		this.ownerEntityId = ownerEntityId;
	}

	/**
	 * Gets the app specific custom data at user level.
	 *
	 * @return the app specific custom data at user level
	 */
	public String getCustomData()
	{
		return customData;
	}

	/**
	 * Sets the app specific custom data at user level.
	 *
	 * @param customData the new app specific custom data at user level
	 */
	public void setCustomData(String customData)
	{
		this.customData = customData;
	}
}
