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

package com.yukthitech.webutils.common.extensions;

import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.persistence.repository.annotations.Operator;
import com.yukthitech.validation.annotations.Required;
import com.yukthitech.webutils.common.annotations.LOV;
import com.yukthitech.webutils.common.annotations.Label;
import com.yukthitech.webutils.common.annotations.Model;

/**
 * Extension field search query.
 * @author akiran
 */
@Model
public class ExtensionFieldSearchQuery
{
	/**
	 * Extension in which search needs to be performed.
	 */
	@Label("Extension")
	@Required
	@LOV(name = "extensionLov")
	@Condition(value = "extension.name", op = Operator.EQ)
	private String extensionName;

	/**
	 * Name field search pattern.
	 */
	@Condition(value = "name", op = Operator.LIKE, ignoreCase = true)
	private String name;

	/**
	 * Only mandatory search fields will be searched.
	 */
	@Condition(value = "required")
	private Boolean required;
	
	/**
	 * Type of the extension fields to be searched.
	 */
	@Condition(value = "type")
	private ExtensionFieldType type;

	/**
	 * Gets the extension in which search needs to be performed.
	 *
	 * @return the extension in which search needs to be performed
	 */
	public String getExtensionName()
	{
		return extensionName;
	}

	/**
	 * Sets the extension in which search needs to be performed.
	 *
	 * @param extensionName the new extension in which search needs to be performed
	 */
	public void setExtensionName(String extensionName)
	{
		this.extensionName = extensionName;
	}

	/**
	 * Gets the name field search pattern.
	 *
	 * @return the name field search pattern
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name field search pattern.
	 *
	 * @param name the new name field search pattern
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Checks if is only mandatory search fields will be searched.
	 *
	 * @return the only mandatory search fields will be searched
	 */
	public Boolean isRequired()
	{
		return required;
	}

	/**
	 * Sets the only mandatory search fields will be searched.
	 *
	 * @param required the new only mandatory search fields will be searched
	 */
	public void setRequired(Boolean required)
	{
		this.required = required;
	}

	/**
	 * Gets the type of the extension fields to be searched.
	 *
	 * @return the type of the extension fields to be searched
	 */
	public ExtensionFieldType getType()
	{
		return type;
	}

	/**
	 * Sets the type of the extension fields to be searched.
	 *
	 * @param type the new type of the extension fields to be searched
	 */
	public void setType(ExtensionFieldType type)
	{
		this.type = type;
	}
}
