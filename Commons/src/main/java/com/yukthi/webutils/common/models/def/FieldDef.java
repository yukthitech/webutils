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

package com.yukthi.webutils.common.models.def;

import java.util.List;

/**
 * Provides details about model field, that can be used by clients for dynamic ui
 * rendering and dynamic validations.
 * 
 * @author akiran
 */
public class FieldDef
{
	/**
	 * Id of the field, in the cases where this field represents extension field
	 */
	private String id;
	
	/**
	 * Name of the model
	 */
	private String name;
	
	/**
	 * Label for user display
	 */
	private String label;
	
	/**
	 * Description for user display
	 */
	private String description;
	
	/**
	 * Data type of current field
	 */
	private FieldType fieldType;
	
	/**
	 * Validations on this field
	 */
	private List<ValidationDef> validations;

	/**
	 * Lov details, if this field type is LOV
	 */
	private LovDetails lovDetails;
	
	/**
	 * Default value for this field
	 */
	private String defaultValue;
	
	/**
	 * if this is read only field. Like id of the entity.
	 */
	private boolean readOnly;
	
	/**
	 * if this is displayable field. For example, password is not displayable.
	 */
	private boolean displayable;

	/**
	 * Gets the id of the field, in the cases where this field represents extension field.
	 *
	 * @return the id of the field, in the cases where this field represents extension field
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * Sets the id of the field, in the cases where this field represents extension field.
	 *
	 * @param id the new id of the field, in the cases where this field represents extension field
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * Gets the name of the model.
	 *
	 * @return the name of the model
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the model.
	 *
	 * @param name the new name of the model
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the label for user display.
	 *
	 * @return the label for user display
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * Sets the label for user display.
	 *
	 * @param label the new label for user display
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}

	/**
	 * Gets the description for user display.
	 *
	 * @return the description for user display
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Sets the description for user display.
	 *
	 * @param description the new description for user display
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * Gets the data type of current field.
	 *
	 * @return the data type of current field
	 */
	public FieldType getFieldType()
	{
		return fieldType;
	}

	/**
	 * Sets the data type of current field.
	 *
	 * @param fieldType the new data type of current field
	 */
	public void setFieldType(FieldType fieldType)
	{
		this.fieldType = fieldType;
	}

	/**
	 * Gets the validations on this field.
	 *
	 * @return the validations on this field
	 */
	public List<ValidationDef> getValidations()
	{
		return validations;
	}

	/**
	 * Sets the validations on this field.
	 *
	 * @param validations the new validations on this field
	 */
	public void setValidations(List<ValidationDef> validations)
	{
		this.validations = validations;
	}

	/**
	 * Gets the lov details, if this field type is LOV.
	 *
	 * @return the lov details, if this field type is LOV
	 */
	public LovDetails getLovDetails()
	{
		return lovDetails;
	}

	/**
	 * Sets the lov details, if this field type is LOV.
	 *
	 * @param lovDetails the new lov details, if this field type is LOV
	 */
	public void setLovDetails(LovDetails lovDetails)
	{
		this.lovDetails = lovDetails;
	}

	/**
	 * Gets the default value for this field.
	 *
	 * @return the default value for this field
	 */
	public String getDefaultValue()
	{
		return defaultValue;
	}

	/**
	 * Sets the default value for this field.
	 *
	 * @param defaultValue the new default value for this field
	 */
	public void setDefaultValue(String defaultValue)
	{
		this.defaultValue = defaultValue;
	}

	/**
	 * Checks if is if this is read only field.
	 *
	 * @return the if this is read only field
	 */
	public boolean isReadOnly()
	{
		return readOnly;
	}

	/**
	 * Sets the if this is read only field.
	 *
	 * @param readOnly the new if this is read only field
	 */
	public void setReadOnly(boolean readOnly)
	{
		this.readOnly = readOnly;
	}

	/**
	 * Checks if is if this is displayable field.
	 *
	 * @return the if this is displayable field
	 */
	public boolean isDisplayable()
	{
		return displayable;
	}

	/**
	 * Sets the if this is displayable field.
	 *
	 * @param displayable the new if this is displayable field
	 */
	public void setDisplayable(boolean displayable)
	{
		this.displayable = displayable;
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
		builder.append(",").append("Label: ").append(label);
		builder.append(",").append("Field Type: ").append(fieldType);
		builder.append(",").append("Def Value: ").append(defaultValue);
		builder.append(",").append("Lov Details: ").append(lovDetails);
		builder.append(",").append("Validations: ").append(validations);

		builder.append("]");
		return builder.toString();
	}

}
