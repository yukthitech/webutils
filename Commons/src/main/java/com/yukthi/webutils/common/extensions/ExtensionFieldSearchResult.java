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

package com.yukthi.webutils.common.extensions;

import java.util.Date;

import com.yukthi.persistence.repository.annotations.Field;
import com.yukthi.webutils.common.annotations.Model;
import com.yukthi.webutils.common.annotations.NonDisplayable;

/**
 * Extension field search result
 * 
 * @author akiran
 */
@Model
public class ExtensionFieldSearchResult
{
	/**
	 * Id of the field
	 */
	@NonDisplayable
	@Field("id")
	private Long id;
	
	/**
	 * Name of the extension field
	 */
	@Field("name")
	private String name;
	
	/**
	 * Label of the extension field
	 */
	@Field("label")
	private String label;

	/**
	 * Type of the extension field
	 */
	@Field("type")
	private ExtensionFieldType type;
	
	/**
	 * Is mandatory field
	 */
	@Field("required")
	private boolean required = false;
	
	/**
	 * Maximum length restriction
	 */
	@Field("maxLength")
	private int maxLength;
	
	/**
	 * Created by user 
	 */
	@Field("createdBy.displayName")
	private String createdBy;
	
	/**
	 * Created on date
	 */
	@Field("createdOn")
	private Date createdOn;
	
	/**
	 * Updated by user
	 */
	@Field("updatedBy.displayName")
	private String updatedBy;
	
	/**
	 * Update on date
	 */
	@Field("updatedOn")
	private Date updatedOn;
	
	/**
	 * Gets the id of the field.
	 *
	 * @return the id of the field
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * Sets the id of the field.
	 *
	 * @param id the new id of the field
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * Gets the name of the extension field.
	 *
	 * @return the name of the extension field
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the extension field.
	 *
	 * @param name the new name of the extension field
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Gets the label of the extension field.
	 *
	 * @return the label of the extension field
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * Sets the label of the extension field.
	 *
	 * @param label the new label of the extension field
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}

	/**
	 * Gets the type of the extension field.
	 *
	 * @return the type of the extension field
	 */
	public ExtensionFieldType getType()
	{
		return type;
	}

	/**
	 * Sets the type of the extension field.
	 *
	 * @param type the new type of the extension field
	 */
	public void setType(ExtensionFieldType type)
	{
		this.type = type;
	}

	/**
	 * Checks if is is mandatory field.
	 *
	 * @return the is mandatory field
	 */
	public boolean isRequired()
	{
		return required;
	}

	/**
	 * Sets the is mandatory field.
	 *
	 * @param required the new is mandatory field
	 */
	public void setRequired(boolean required)
	{
		this.required = required;
	}

	/**
	 * Gets the maximum length restriction.
	 *
	 * @return the maximum length restriction
	 */
	public int getMaxLength()
	{
		return maxLength;
	}

	/**
	 * Sets the maximum length restriction.
	 *
	 * @param maxLength the new maximum length restriction
	 */
	public void setMaxLength(int maxLength)
	{
		this.maxLength = maxLength;
	}

	/**
	 * Gets the created by user.
	 *
	 * @return the created by user
	 */
	public String getCreatedBy()
	{
		return createdBy;
	}

	/**
	 * Sets the created by user.
	 *
	 * @param createdBy the new created by user
	 */
	public void setCreatedBy(String createdBy)
	{
		this.createdBy = createdBy;
	}

	/**
	 * Gets the created on date.
	 *
	 * @return the created on date
	 */
	public Date getCreatedOn()
	{
		return createdOn;
	}

	/**
	 * Sets the created on date.
	 *
	 * @param createdOn the new created on date
	 */
	public void setCreatedOn(Date createdOn)
	{
		this.createdOn = createdOn;
	}

	/**
	 * Gets the updated by user.
	 *
	 * @return the updated by user
	 */
	public String getUpdatedBy()
	{
		return updatedBy;
	}

	/**
	 * Sets the updated by user.
	 *
	 * @param updatedBy the new updated by user
	 */
	public void setUpdatedBy(String updatedBy)
	{
		this.updatedBy = updatedBy;
	}

	/**
	 * Gets the update on date.
	 *
	 * @return the update on date
	 */
	public Date getUpdatedOn()
	{
		return updatedOn;
	}

	/**
	 * Sets the update on date.
	 *
	 * @param updatedOn the new update on date
	 */
	public void setUpdatedOn(Date updatedOn)
	{
		this.updatedOn = updatedOn;
	}
}
