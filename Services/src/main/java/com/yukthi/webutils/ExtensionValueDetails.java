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

package com.yukthi.webutils;

import com.yukthi.persistence.repository.annotations.Field;

/**
 * Used internally by services to fetch existing 
 * @author akiran
 */
public class ExtensionValueDetails
{
	/**
	 * Id of the extension field value
	 */
	@Field("id")
	private long id;
	
	/**
	 * Version of extension field value
	 */
	@Field("version")
	private int version;
	
	/**
	 * Extension field id
	 */
	@Field("extensionField.id")
	private long extensionFieldId;
	
	/**
	 * Extension field name
	 */
	@Field("extensionField.name")
	private String extensionFieldName;
	
	/**
	 * Value of the extension field
	 */
	@Field("value")
	private String value;

	/**
	 * Gets the id of the extension field value.
	 *
	 * @return the id of the extension field value
	 */
	public long getId()
	{
		return id;
	}

	/**
	 * Sets the id of the extension field value.
	 *
	 * @param id the new id of the extension field value
	 */
	public void setId(long id)
	{
		this.id = id;
	}
	

	/**
	 * Gets the version of extension field value.
	 *
	 * @return the version of extension field value
	 */
	public int getVersion()
	{
		return version;
	}

	/**
	 * Sets the version of extension field value.
	 *
	 * @param version the new version of extension field value
	 */
	public void setVersion(int version)
	{
		this.version = version;
	}

	/**
	 * Gets the extension field id.
	 *
	 * @return the extension field id
	 */
	public long getExtensionFieldId()
	{
		return extensionFieldId;
	}

	/**
	 * Sets the extension field id.
	 *
	 * @param extensionFieldId the new extension field id
	 */
	public void setExtensionFieldId(long extensionFieldId)
	{
		this.extensionFieldId = extensionFieldId;
	}

	/**
	 * Gets the extension field name.
	 *
	 * @return the extension field name
	 */
	public String getExtensionFieldName()
	{
		return extensionFieldName;
	}

	/**
	 * Sets the extension field name.
	 *
	 * @param extensionFieldName the new extension field name
	 */
	public void setExtensionFieldName(String extensionFieldName)
	{
		this.extensionFieldName = extensionFieldName;
	}

	/**
	 * Gets the value of the extension field.
	 *
	 * @return the value of the extension field
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * Sets the value of the extension field.
	 *
	 * @param value the new value of the extension field
	 */
	public void setValue(String value)
	{
		this.value = value;
	}
	
	
}
