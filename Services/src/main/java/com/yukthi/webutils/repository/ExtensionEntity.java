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

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.yukthi.persistence.annotations.DataType;
import com.yukthi.persistence.annotations.DataTypeMapping;
import com.yukthi.persistence.annotations.UniqueConstraint;
import com.yukthi.persistence.annotations.UniqueConstraints;
import com.yukthi.persistence.conversion.impl.JsonConverter;

/**
 * Entity to keep track of the extensions of other entities 
 * @author akiran
 */
@Table(name = "ENTITY_EXTENSIONS")
@UniqueConstraints({
	@UniqueConstraint(name = "OWNR_TYPE_OWNR_ID", fields = {"targetEntity", "ownerEntityType", "ownerId"})
})
public class ExtensionEntity
{
	/**
	 * Primary key of the entity
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private long id;
	
	/**
	 * Name of the target entity for which this extended field is defined
	 */
	@Column(name = "TARGET_ENTITY_NAME", nullable = false, length = 500)
	private String targetEntity;
	
	/**
	 * Owner entity type for which extension is being defined
	 */
	@Column(name = "OWNER_ENTITY_TYPE", nullable = false, length = 500)
	private String ownerEntityType;
	
	/**
	 * Owner entity id for which entity is being defined. 
	 */
	@Column(name = "OWNER_ENTITY_ID", nullable = false)
	private long ownerId;
	
	/**
	 * optional name for the extension
	 */
	@Column(name = "NAME", nullable = true)
	private String name;
	
	/**
	 * Custom attributes for the extension
	 */
	@Column(name = "CUSTOM_ATTR", length = 2000)
	@DataTypeMapping(type = DataType.STRING, converterType = JsonConverter.class)
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
		this.id = id;
	}

	/**
	 * Instantiates a new extension entity.
	 *
	 * @param targetEntity the target entity
	 * @param ownerEntityType the owner entity type
	 * @param ownerId the owner id
	 */
	public ExtensionEntity(String targetEntity, String ownerEntityType, long ownerId)
	{
		this.targetEntity = targetEntity;
		this.ownerEntityType = ownerEntityType;
		this.ownerId = ownerId;
	}

	/**
	 * Gets the primary key of the entity.
	 *
	 * @return the primary key of the entity
	 */
	public long getId()
	{
		return id;
	}

	/**
	 * Sets the primary key of the entity.
	 *
	 * @param id the new primary key of the entity
	 */
	public void setId(long id)
	{
		this.id = id;
	}

	/**
	 * Gets the name of the target entity for which this extended field is defined.
	 *
	 * @return the name of the target entity for which this extended field is defined
	 */
	public String getTargetEntity()
	{
		return targetEntity;
	}

	/**
	 * Sets the name of the target entity for which this extended field is defined.
	 *
	 * @param targetEntity the new name of the target entity for which this extended field is defined
	 */
	public void setTargetEntity(String targetEntity)
	{
		this.targetEntity = targetEntity;
	}

	/**
	 * Gets the owner entity type for which extension is being defined.
	 *
	 * @return the owner entity type for which extension is being defined
	 */
	public String getOwnerEntityType()
	{
		return ownerEntityType;
	}

	/**
	 * Sets the owner entity type for which extension is being defined.
	 *
	 * @param ownerEntityType the new owner entity type for which extension is being defined
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
	public long getOwnerId()
	{
		return ownerId;
	}

	/**
	 * Sets the owner entity id for which entity is being defined.
	 *
	 * @param ownerId the new owner entity id for which entity is being defined
	 */
	public void setOwnerId(long ownerId)
	{
		this.ownerId = ownerId;
	}

	/**
	 * Gets the optional name for the extension.
	 *
	 * @return the optional name for the extension
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the optional name for the extension.
	 *
	 * @param name the new optional name for the extension
	 */
	public void setName(String name)
	{
		this.name = name;
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