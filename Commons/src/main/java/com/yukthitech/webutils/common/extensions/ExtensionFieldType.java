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

import java.util.Date;

import com.yukthitech.webutils.common.IWebUtilsCommonConstants;

/**
 * Enumerates list of supported extension field types
 * @author akiran
 */
public enum ExtensionFieldType
{
	/**
	 * Simple string field
	 */
	STRING(String.class)
	{
		@Override
		public boolean validateValue(String value, FieldConfiguration fieldConfiguration)
		{
			return value.length() <= fieldConfiguration.getMaxLength();
		}
	},
	
	/**
	 * Multi lined string.
	 */
	MULTI_LINE_STRING(String.class)
	{
		@Override
		public boolean validateValue(String value, FieldConfiguration fieldConfiguration)
		{
			return value.length() <= fieldConfiguration.getMaxLength();
		}
	},
	
	/** The integer. */
	INTEGER(Integer.class)
	{
		@Override
		public boolean validateValue(String value, FieldConfiguration fieldConfiguration)
		{
			return IWebUtilsCommonConstants.INT_PATTERN.matcher(value).matches();
		}
	},
	
	/** The decimal. */
	DECIMAL(Double.class)
	{
		@Override
		public boolean validateValue(String value, FieldConfiguration fieldConfiguration)
		{
			return IWebUtilsCommonConstants.DECIMAL_PATTERN.matcher(value).matches() ||
					IWebUtilsCommonConstants.INT_PATTERN.matcher(value).matches();
		}
	},
	
	/** The boolean. */
	BOOLEAN(Boolean.class)
	{
		@Override
		public boolean validateValue(String value, FieldConfiguration fieldConfiguration)
		{
			return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
		}
	},
	
	/** The date. */
	DATE(Date.class)
	{
		@Override
		public boolean validateValue(String value, FieldConfiguration fieldConfiguration)
		{
			try
			{
				fieldConfiguration.getDateFormat().parse(value);
				return true;
			}catch(Exception ex)
			{
				return false;
			}
		}
	},
	
	/** The list of values. */
	LIST_OF_VALUES(String.class)
	{
		@Override
		public boolean validateValue(String value, FieldConfiguration fieldConfiguration)
		{
			return fieldConfiguration.getLovValues().contains(value);
		}
	};
	
	/**
	 * Mapping java type for current extension type.
	 */
	private Class<?> javaType;
	
	/**
	 * Instantiates a new extension field type.
	 *
	 * @param javaType the java type
	 */
	private ExtensionFieldType(Class<?> javaType)
	{
		this.javaType = javaType;
	}
	
	/**
	 * Gets the mapping java type for current extension type.
	 *
	 * @return the mapping java type for current extension type
	 */
	public Class<?> getJavaType()
	{
		return javaType;
	}

	/**
	 * Validate value.
	 *
	 * @param value the value
	 * @param fieldConfiguration the field configuration
	 * @return true, if successful
	 */
	public abstract boolean validateValue(String value, FieldConfiguration fieldConfiguration);
}
