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

import java.text.SimpleDateFormat;
import java.util.Set;

/**
 * Field configuration that can be used for field value validation
 * @author akiran
 */
public class FieldConfiguration
{
	/**
	 * Date format to be used for fields
	 */
	private SimpleDateFormat dateFormat;
	
	/**
	 * Configured LOV values
	 */
	private Set<String> lovValues;
	
	/**
	 * Configured field max length
	 */
	private int maxLength;

	/**
	 * Instantiates a new field configuration.
	 *
	 * @param dateFormat the date format
	 * @param lovValues the lov values
	 * @param maxLength the max length
	 */
	public FieldConfiguration(SimpleDateFormat dateFormat, Set<String> lovValues, int maxLength)
	{
		this.dateFormat = dateFormat;
		this.lovValues = lovValues;
		this.maxLength = maxLength;
	}

	/**
	 * Gets the date format to be used for fields.
	 *
	 * @return the date format to be used for fields
	 */
	public SimpleDateFormat getDateFormat()
	{
		return dateFormat;
	}

	/**
	 * Gets the configured LOV values.
	 *
	 * @return the configured LOV values
	 */
	public Set<String> getLovValues()
	{
		return lovValues;
	}

	/**
	 * Gets the configured field max length.
	 *
	 * @return the configured field max length
	 */
	public int getMaxLength()
	{
		return maxLength;
	}
}
