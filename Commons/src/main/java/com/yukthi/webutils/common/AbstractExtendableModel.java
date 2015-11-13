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
	private Map<Long, String> idToVal = new HashMap<>();
	
	/**
	 * Method to add extended field value
	 * @param fieldId Extended field id
	 * @param value Value for extended field
	 */
	public void addExtendedField(Long fieldId, Object value)
	{
		if(value == null)
		{
			idToVal.remove(fieldId);
			return;
		}
		
		idToVal.put(fieldId, value.toString());
	}
	
	/* (non-Javadoc)
	 * @see com.yukthi.webutils.common.IExtendableModel#getExtendedFields()
	 */
	@Override
	public Map<Long, String> getExtendedFields()
	{
		return idToVal;
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.common.IExtendableModel#setExtendedFields(java.util.Map)
	 */
	@Override
	public void setExtendedFields(Map<Long, String> extendedFieldValues)
	{
		this.idToVal.clear();
		this.idToVal.putAll(extendedFieldValues);
	}
}
