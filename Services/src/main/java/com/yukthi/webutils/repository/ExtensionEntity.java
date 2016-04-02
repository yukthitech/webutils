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
import javax.persistence.Table;

import com.yukthi.persistence.annotations.DataType;
import com.yukthi.persistence.annotations.DataTypeMapping;
import com.yukthi.persistence.annotations.Index;
import com.yukthi.persistence.annotations.Indexes;
import com.yukthi.persistence.annotations.UniqueConstraint;
import com.yukthi.persistence.annotations.UniqueConstraints;
import com.yukthi.persistence.conversion.impl.JsonConverter;

/**
 * Entity to keep track of the extensions of other entities. 
 * @author akiran
 */
@Table(name = "ENTITY_EXTENSIONS")
@UniqueConstraints({
	@UniqueConstraint(name = "NAME", fields = {"name"})
})
@Indexes({
	@Index(name = "OWNR_TYPE_OWNR_ID", fields = {"targetPointName", "ownerPointName", "ownerId"})
})
public class ExtensionEntity extends WebutilsEntity
{
	/**
	 * Name of the target entity point for which this extended field is defined.
	 */
	@Column(name = "TARGET_POINT_NAME", nullable = false, length = 250)
	private String targetPointName;
	
	/**
	 * Owner entity point name under which extension is being defined.
	 */
	@Column(name = "OWNER_POINT_NAME", nullable = false, length = 100)
	private String ownerPointName;
	
	/**
	 * Owner entity id for which entity is being defined. 
	 */
	@Column(name = "OWNER_ENTITY_ID", nullable = false)
	private long ownerId;
	
	/**
	 * Name for the extension.
	 */
	@Column(name = "NAME", nullable = true)
	private String name;
	
	/**
	 * Custom attributes for the extension.
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
		super(id);
	}

	/**
	 * Instantiates a new extension entity.
	 *
	 * @param targetPointName the target point name
	 * @param ownerPointName the owner point name
	 * @param ownerId the owner id
	 */
	public ExtensionEntity(String targetPointName, String ownerPointName, long ownerId)
	{
		this.targetPointName = targetPointName;
		this.ownerPointName = ownerPointName;
		this.ownerId = ownerId;
	}

	/**
	 * Gets the name of the target entity point for which this extended field is defined.
	 *
	 * @return the name of the target entity point for which this extended field is defined
	 */
	public String getTargetPointName()
	{
		return targetPointName;
	}

	/**
	 * Sets the name of the target entity point for which this extended field is defined.
	 *
	 * @param targetPointName the new name of the target entity point for which this extended field is defined
	 */
	public void setTargetPointName(String targetPointName)
	{
		this.targetPointName = targetPointName;
	}

	/**
	 * Gets the owner entity point name under which extension is being defined.
	 *
	 * @return the owner entity point name under which extension is being defined
	 */
	public String getOwnerPointName()
	{
		return ownerPointName;
	}

	/**
	 * Sets the owner entity point name under which extension is being defined.
	 *
	 * @param ownerPointName the new owner entity point name under which extension is being defined
	 */
	public void setOwnerPointName(String ownerPointName)
	{
		this.ownerPointName = ownerPointName;
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
