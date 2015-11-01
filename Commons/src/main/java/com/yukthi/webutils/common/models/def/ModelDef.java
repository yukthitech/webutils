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
 * Provides information about the model type, using which client can render dynamic ui
 * and perform dynamic validations.
 * @author akiran
 */
public class ModelDef
{
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
	 * List of fields of this model
	 */
	private List<FieldDef> fields;

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
	 * Gets the list of fields of this model.
	 *
	 * @return the list of fields of this model
	 */
	public List<FieldDef> getFields()
	{
		return fields;
	}

	/**
	 * Sets the list of fields of this model.
	 *
	 * @param fields the new list of fields of this model
	 */
	public void setFields(List<FieldDef> fields)
	{
		this.fields = fields;
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
		builder.append(",").append("Fields: ").append(fields);

		builder.append("]");
		return builder.toString();
	}

}
