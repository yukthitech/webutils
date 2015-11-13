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

/**
 * Extesion fields response from server
 * @author akiran
 */
public class ExtensionFieldsResponse extends BaseResponse
{
	/**
	 * List of extension fields
	 */
	private List<ExtensionFieldModel> extensionFields;
	
	/**
	 * Instantiates a new extension fields response.
	 */
	public ExtensionFieldsResponse()
	{}

	/**
	 * Instantiates a new extension fields response.
	 *
	 * @param extensionFields the extension fields
	 */
	public ExtensionFieldsResponse(List<ExtensionFieldModel> extensionFields)
	{
		this.extensionFields = extensionFields;
	}

	/**
	 * Gets the list of extension fields.
	 *
	 * @return the list of extension fields
	 */
	public List<ExtensionFieldModel> getExtensionFields()
	{
		return extensionFields;
	}

	/**
	 * Sets the list of extension fields.
	 *
	 * @param extensionFields the new list of extension fields
	 */
	public void setExtensionFields(List<ExtensionFieldModel> extensionFields)
	{
		this.extensionFields = extensionFields;
	}
}
