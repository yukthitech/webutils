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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.yukthitech.persistence.annotations.DataType;
import com.yukthitech.persistence.annotations.DataTypeMapping;
import com.yukthitech.persistence.annotations.DeleteWithParent;
import com.yukthitech.persistence.annotations.NotUpdateable;
import com.yukthitech.persistence.annotations.UniqueConstraint;
import com.yukthitech.persistence.annotations.UniqueConstraints;
import com.yukthitech.persistence.conversion.impl.JsonWithTypeConverter;
import com.yukthitech.webutils.common.annotations.Optional;
import com.yukthitech.webutils.common.extensions.ExtensionFieldType;
import com.yukthitech.webutils.common.extensions.LovOption;

/**
 * Represents extension field entity.
 * 
 * @author akiran
 */
@Table(name = "WEBUTILS_EXTENSION_FIELDS")
@UniqueConstraints({
	@UniqueConstraint(name = "EXT_ID_NAME", fields = {"extension", "name"}, message = "Extension field with specified name already eixsts.", finalName = false),
	@UniqueConstraint(name = "EXT_ID_COL_NAME", fields = {"extension", "columnName"}, message = "Extension field for specified column already exists.", finalName = false),
	@UniqueConstraint(name = "EXT_ID_LABEL", fields = {"extension", "label"}, message = "Extension field with specified label already eixsts.", finalName = false),
	})
@Optional
public class ExtensionFieldEntity extends WebutilsEntity
{
	/**
	 * Extension for which this field is being defined.
	 */
	@NotUpdateable
	@ManyToOne
	@Column(name = "EXTENSION_ID", nullable = false)
	@DeleteWithParent
	private ExtensionEntity extension;

	/**
	 * Name of the field.
	 */
	@Column(name = "NAME", nullable = false, length = 50)
	private String name;
	
	/**
	 * Extended field name of the entity.
	 */
	@NotUpdateable
	@Column(name = "EXT_COL_NAME", nullable = false, length = 50)
	private String columnName;

	/**
	 * Label of the field.
	 */
	@Column(name = "LABEL", nullable = false, length = 50)
	private String label;

	/**
	 * Description of the field.
	 */
	@Column(name = "DESCRIPTION", length = 500)
	private String description;

	/**
	 * Field type.
	 */
	@Column(name = "DATA_TYPE", length = 50)
	@DataTypeMapping(type = DataType.STRING)
	private ExtensionFieldType type;

	/**
	 * Indicates whether this field is mandatory field.
	 */
	@Column(name = "IS_REQUIRED")
	private boolean required = false;

	/**
	 * LOV options for fields of LOV type.
	 */
	@Column(name = "LOV_OPTIONS", length = 2000)
	@DataTypeMapping(type = DataType.STRING, converterType = JsonWithTypeConverter.class)
	private List<LovOption> lovOptions;

	/**
	 * Internal field to be used to fetch unique set of lov values.
	 */
	@Transient
	private Set<String> lovValues;
	
	/**
	 * Value Max length limit on field.
	 */
	private int maxLength;
	
	/**
	 * Instantiates a new extension field entity.
	 */
	public ExtensionFieldEntity()
	{}
	
	/**
	 * Instantiates a new extension field entity.
	 *
	 * @param id the id
	 */
	public ExtensionFieldEntity(Long id)
	{
		super(id);
	}
	
	/**
	 * Gets the extension for which this field is being defined.
	 *
	 * @return the extension for which this field is being defined
	 */
	public ExtensionEntity getExtension()
	{
		return extension;
	}

	/**
	 * Sets the extension for which this field is being defined.
	 *
	 * @param extension
	 *            the new extension for which this field is being defined
	 */
	public void setExtension(ExtensionEntity extension)
	{
		this.extension = extension;
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
	 * Gets the extended field name of the entity.
	 *
	 * @return the extended field name of the entity
	 */
	public String getColumnName()
	{
		return columnName;
	}

	/**
	 * Sets the extended field name of the entity.
	 *
	 * @param columnName the new extended field name of the entity
	 */
	public void setColumnName(String columnName)
	{
		this.columnName = columnName;
	}

	/**
	 * Gets the label of the field.
	 *
	 * @return the label of the field
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * Sets the label of the field.
	 *
	 * @param label the new label of the field
	 */
	public void setLabel(String label)
	{
		this.label = label;
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

	/**
	 * Gets the internal field to be used to fetch unique set of lov values.
	 *
	 * @return the internal field to be used to fetch unique set of lov values
	 */
	public Set<String> getLovValues()
	{
		if(type != ExtensionFieldType.LIST_OF_VALUES)
		{
			return null;
		}
		
		if(lovValues != null)
		{
			return lovValues;
		}
		
		Set<String> lovValues = new HashSet<>();
		
		for(LovOption option : this.lovOptions)
		{
			lovValues.add(option.getValue());
		}
		
		this.lovValues = lovValues;
		return lovValues;
	}

	/**
	 * Gets the value Max length limit on field.
	 *
	 * @return the value Max length limit on field
	 */
	public int getMaxLength()
	{
		return maxLength;
	}

	/**
	 * Sets the value Max length limit on field.
	 *
	 * @param maxLength the new value Max length limit on field
	 */
	public void setMaxLength(int maxLength)
	{
		this.maxLength = maxLength;
	}
}
