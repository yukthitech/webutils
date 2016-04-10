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

import com.yukthi.webutils.controllers.ExtensionController;
import com.yukthi.webutils.controllers.IExtensionContextProvider;

/**
 * Extension details to be provided by Web-applications via {@link IExtensionContextProvider} implementation. Which
 * in turn is used by {@link ExtensionController}
 * 
 * @author akiran
 */
public class ExtensionDetails
{
	/**
	 * Owner entity type. If no ownership is required, this can be null
	 */
	private Class<?> ownerType;

	/**
	 * Id of the owner. If no ownership is required, this can be zero
	 */
	private long ownerId;

	/**
	 * Name of the extension. Optional. If specified, same will be used for
	 * extension entity
	 */
	private String name;

	/**
	 * Customized attributes for extension.
	 */
	private Object attributes;
	
	/**
	 * Instantiates a new extension.
	 */
	public ExtensionDetails()
	{}
	
	/**
	 * Instantiates a new extension.
	 *
	 * @param ownerType the owner type
	 * @param ownerId the owner id
	 */
	public ExtensionDetails(Class<?> ownerType, long ownerId)
	{
		this.ownerType = ownerType;
		this.ownerId = ownerId;
	}

	/**
	 * Instantiates a new extension.
	 *
	 * @param ownerType the owner type
	 * @param ownerId the owner id
	 * @param name the name
	 * @param attributes the attributes
	 */
	public ExtensionDetails(Class<?> ownerType, long ownerId, String name, Object attributes)
	{
		this.ownerType = ownerType;
		this.ownerId = ownerId;
		this.name = name;
		this.attributes = attributes;
	}

	/**
	 * Gets the owner entity type.
	 *
	 * @return the owner entity type
	 */
	public Class<?> getOwnerType()
	{
		return ownerType;
	}
	
	/**
	 * Gets the owner type name.
	 *
	 * @return the owner type name
	 */
	public String getOwnerTypeName()
	{
		return (ownerType != null) ? ownerType.getName() : null;
	}

	/**
	 * Sets the owner entity type.
	 *
	 * @param ownerType the new owner entity type
	 */
	public void setOwnerType(Class<?> ownerType)
	{
		this.ownerType = ownerType;
	}

	/**
	 * Gets the id of the owner.
	 *
	 * @return the id of the owner
	 */
	public long getOwnerId()
	{
		return ownerId;
	}

	/**
	 * Sets the id of the owner.
	 *
	 * @param ownerId the new id of the owner
	 */
	public void setOwnerId(long ownerId)
	{
		this.ownerId = ownerId;
	}

	/**
	 * Gets the name of the extension.
	 *
	 * @return the name of the extension
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the extension.
	 *
	 * @param name the new name of the extension
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the customized attributes for extension.
	 *
	 * @return the customized attributes for extension
	 */
	public Object getAttributes()
	{
		return attributes;
	}

	/**
	 * Sets the customized attributes for extension.
	 *
	 * @param attributes the new customized attributes for extension
	 */
	public void setAttributes(Object attributes)
	{
		this.attributes = attributes;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Owner Type: ").append(ownerType.getName());
		builder.append(",").append("Owner id: ").append(ownerId);
		builder.append(",").append("Name: ").append(name);

		builder.append("]");
		return builder.toString();
	}
}
 