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
import javax.persistence.Table;

import com.yukthitech.persistence.annotations.DataType;
import com.yukthitech.persistence.annotations.DataTypeMapping;
import com.yukthitech.persistence.annotations.NotUpdateable;
import com.yukthitech.persistence.annotations.UniqueConstraint;
import com.yukthitech.persistence.annotations.UniqueConstraints;
import com.yukthitech.persistence.conversion.impl.JsonWithTypeConverter;

/**
 * Entity to keep track of the extensions of other entities. 
 * @author akiran
 */
@Table(name = "WEBUTILS_ENTITY_EXTENSIONS")
@UniqueConstraints({
	@UniqueConstraint(name = "TARGET_OWNER", fields = {"targetEntityType", "ownerEntityType", "ownerEntityId"}),
	@UniqueConstraint(name = "NAME", fields = {"spaceIdentity", "name"})
	})
public class ExtensionEntity extends WebutilsEntity
{
	/**
	 * Name of the extension.
	 */
	@Column(name = "NAME", nullable = false, length = 250)
	private String name;
	
	/**
	 * Name of the target entity type for which this extended field is defined.
	 */
	@NotUpdateable
	@Column(name = "TARGET_ENTITY_TYPE", nullable = false, length = 250)
	private String targetEntityType;
	
	/**
	 * Owner entity type under which extension is being defined.
	 */
	@NotUpdateable
	@Column(name = "OWNER_ENTITY_TYPE", nullable = false, length = 100)
	private String ownerEntityType;
	
	/**
	 * Owner entity id for which entity is being defined. 
	 */
	@NotUpdateable
	@Column(name = "OWNER_ENTITY_ID", nullable = false)
	private long ownerEntityId;
	
	/**
	 * Custom attributes for the extension.
	 */
	@NotUpdateable
	@Column(name = "CUSTOM_ATTR", length = 2000)
	@DataTypeMapping(type = DataType.STRING, converterType = JsonWithTypeConverter.class)
	private Object attributes;

	/**
	 * Instantiates a new extension entity.
	 */
	public ExtensionEntity()
	{}
	
	/**
	 * Instantiates a new extension entity.
	 *
	 * @param id the id
	 */
	public ExtensionEntity(long id)
	{
		super(id);
	}

	/**
	 * Instantiates a new extension entity.
	 *
	 * @param name Name of the extension
	 * @param targetEntityType the target entity type
	 * @param ownerEntityType the owner entity type
	 * @param ownerEntityId the owner entity id
	 * @param attributes the attributes
	 */
	public ExtensionEntity(String name, String targetEntityType, String ownerEntityType, long ownerEntityId, Object attributes)
	{
		this.name = name;
		this.targetEntityType = targetEntityType;
		this.ownerEntityType = ownerEntityType;
		this.ownerEntityId = ownerEntityId;
		this.attributes = attributes;
	}
	
	/**
	 * Gets the name of the extension.
	 *
	 * @return the name of the extension
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the extension.
	 *
	 * @param name the new name of the extension
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the name of the target entity type for which this extended field is defined.
	 *
	 * @return the name of the target entity type for which this extended field is defined
	 */
	public String getTargetEntityType()
	{
		return targetEntityType;
	}

	/**
	 * Sets the name of the target entity type for which this extended field is defined.
	 *
	 * @param targetEntityType the new name of the target entity type for which this extended field is defined
	 */
	public void setTargetEntityType(String targetEntityType)
	{
		this.targetEntityType = targetEntityType;
	}

	/**
	 * Gets the owner entity type under which extension is being defined.
	 *
	 * @return the owner entity type under which extension is being defined
	 */
	public String getOwnerEntityType()
	{
		return ownerEntityType;
	}

	/**
	 * Sets the owner entity type under which extension is being defined.
	 *
	 * @param ownerEntityType the new owner entity type under which extension is being defined
	 */
	public void setOwnerEntityType(String ownerEntityType)
	{
		this.ownerEntityType = ownerEntityType;
	}

	/**
	 * Gets the owner entity id for which entity is being defined.
	 *
	 * @return the owner entity id for which entity is being defined
	 */
	public long getOwnerEntityId()
	{
		return ownerEntityId;
	}

	/**
	 * Sets the owner entity id for which entity is being defined.
	 *
	 * @param ownerEntityId the new owner entity id for which entity is being defined
	 */
	public void setOwnerEntityId(long ownerEntityId)
	{
		this.ownerEntityId = ownerEntityId;
	}

	/**
	 * Gets the custom attributes for the extension.
	 *
	 * @return the custom attributes for the extension
	 */
	public Object getAttributes()
	{
		return attributes;
	}

	/**
	 * Sets the custom attributes for the extension.
	 *
	 * @param attributes the new custom attributes for the extension
	 */
	public void setAttributes(Object attributes)
	{
		this.attributes = attributes;
	}
}
