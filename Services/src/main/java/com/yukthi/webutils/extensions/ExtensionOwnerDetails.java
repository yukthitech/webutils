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

package com.yukthi.webutils.extensions;

/**
 * Extension owner details
 * @author akiran
 */
public class ExtensionOwnerDetails
{
	/**
	 * Name of the extension owner
	 */
	private String name;
	
	/**
	 * Entity type which is defined as extension owner
	 */
	private Class<?> entityType;
	
	/**
	 * Instantiates a new extension owner details.
	 */
	public ExtensionOwnerDetails()
	{}
	
	/**
	 * Instantiates a new extension owner details.
	 *
	 * @param name the name
	 * @param entityType the entity type
	 */
	public ExtensionOwnerDetails(String name, Class<?> entityType)
	{
		this.name = name;
		this.entityType = entityType;
	}

	/**
	 * Gets the name of the extension owner.
	 *
	 * @return the name of the extension owner
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the extension owner.
	 *
	 * @param name the new name of the extension owner
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the entity type which is defined as extension owner.
	 *
	 * @return the entity type which is defined as extension owner
	 */
	public Class<?> getEntityType()
	{
		return entityType;
	}

	/**
	 * Sets the entity type which is defined as extension owner.
	 *
	 * @param entityType the new entity type which is defined as extension owner
	 */
	public void setEntityType(Class<?> entityType)
	{
		this.entityType = entityType;
	}

	
}
