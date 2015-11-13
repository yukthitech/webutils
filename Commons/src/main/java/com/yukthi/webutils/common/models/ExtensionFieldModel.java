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

package com.yukthi.webutils.common.models;

import java.util.List;

import com.yukthi.validation.annotations.MaxLen;
import com.yukthi.validation.annotations.MinLen;
import com.yukthi.validation.annotations.NotEmpty;
import com.yukthi.validation.annotations.Required;
import com.yukthi.webutils.common.annotations.IgnoreField;
import com.yukthi.webutils.common.annotations.Model;
import com.yukthi.webutils.common.annotations.MultilineText;
import com.yukthi.webutils.common.extensions.ExtensionFieldType;
import com.yukthi.webutils.common.extensions.LovOption;

/**
 * Represents extension field entity
 * 
 * @author akiran
 */
@Model
public class ExtensionFieldModel
{
	/**
	 * Primary key of the entity
	 */
	private long id;

	/**
	 * Name of the field
	 */
	@NotEmpty
	@MinLen(3)
	@MaxLen(50)
	private String name;

	/**
	 * Description of the field
	 */
	@MultilineText
	@MaxLen(500)
	private String description;

	/**
	 * Field type
	 */
	@Required
	private ExtensionFieldType type;

	/**
	 * Indicates whether this field is mandatory field
	 */
	private boolean required = false;

	/**
	 * LOV options for fields of LOV type.
	 */
	@IgnoreField
	private List<LovOption> lovOptions;
	
	public ExtensionFieldModel()
	{}
	
	/**
	 * Instantiates a new extension field model. Can be used for simple fields.
	 *
	 * @param name the name
	 * @param description the description
	 * @param type the type
	 * @param required the required
	 */
	public ExtensionFieldModel(String name, String description, ExtensionFieldType type, boolean required)
	{
		this.name = name;
		this.description = description;
		this.type = type;
		this.required = required;
	}

	/**
	 * Instantiates a new extension field model. Can be used for LOV fields.
	 *
	 * @param name the name
	 * @param description the description
	 * @param required the required
	 * @param lovOptions the lov options
	 */
	public ExtensionFieldModel(String name, String description, boolean required, List<LovOption> lovOptions)
	{
		this.name = name;
		this.description = description;
		this.type = ExtensionFieldType.LIST_OF_VALUES;
		this.required = required;
		this.lovOptions = lovOptions;
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
	 * @param id
	 *            the new primary key of the entity
	 */
	public void setId(long id)
	{
		this.id = id;
	}

	/**
	 * Gets the name of the field.
	 *
	 * @return the name of the field
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the field.
	 *
	 * @param name
	 *            the new name of the field
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the description of the field.
	 *
	 * @return the description of the field
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Sets the description of the field.
	 *
	 * @param description
	 *            the new description of the field
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * Gets the field type.
	 *
	 * @return the field type
	 */
	public ExtensionFieldType getType()
	{
		return type;
	}

	/**
	 * Sets the field type.
	 *
	 * @param type
	 *            the new field type
	 */
	public void setType(ExtensionFieldType type)
	{
		this.type = type;
	}

	/**
	 * Checks if is indicates whether this field is mandatory field.
	 *
	 * @return the indicates whether this field is mandatory field
	 */
	public boolean isRequired()
	{
		return required;
	}

	/**
	 * Sets the indicates whether this field is mandatory field.
	 *
	 * @param required
	 *            the new indicates whether this field is mandatory field
	 */
	public void setRequired(boolean required)
	{
		this.required = required;
	}

	/**
	 * Gets the lOV options for fields of LOV type.
	 *
	 * @return the lOV options for fields of LOV type
	 */
	public List<LovOption> getLovOptions()
	{
		return lovOptions;
	}

	/**
	 * Sets the lOV options for fields of LOV type.
	 *
	 * @param lovOptions the new lOV options for fields of LOV type
	 */
	public void setLovOptions(List<LovOption> lovOptions)
	{
		this.lovOptions = lovOptions;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Name: ").append(name);
		builder.append(",").append("Id: ").append(id);
		builder.append(",").append("Description: ").append(description);
		builder.append(",").append("Type: ").append(type);
		builder.append(",").append("Required: ").append(required);
		builder.append(",").append("LOV Options: ").append(lovOptions);
		

		builder.append("]");
		return builder.toString();
	}

}
