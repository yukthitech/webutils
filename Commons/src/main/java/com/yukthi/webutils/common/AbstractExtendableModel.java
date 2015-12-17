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

package com.yukthi.webutils.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract base class foe extendable model
 * @author akiran
 */
public abstract class AbstractExtendableModel implements IExtendableModel
{
	/**
	 * Map to hold extended field value
	 */
	private Map<String, String> nameToVal = new HashMap<>();
	
	/**
	 * Method to add extended field value
	 * @param fieldName Extended field name
	 * @param value Value for extended field
	 */
	public void setExtendedField(String fieldName, Object value)
	{
		if(value == null)
		{
			nameToVal.remove(fieldName);
			return;
		}
		
		nameToVal.put(fieldName, value.toString());
	}
	
	/* (non-Javadoc)
	 * @see com.yukthi.webutils.common.IExtendableModel#getExtendedFields()
	 */
	@Override
	public Map<String, String> getExtendedFields()
	{
		return nameToVal;
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.common.IExtendableModel#setExtendedFields(java.util.Map)
	 */
	@Override
	public void setExtendedFields(Map<String, String> extendedFieldValues)
	{
		this.nameToVal.clear();
		this.nameToVal.putAll(extendedFieldValues);
	}
}
