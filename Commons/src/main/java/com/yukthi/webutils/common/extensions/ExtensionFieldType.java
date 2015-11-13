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
package com.yukthi.webutils.common.extensions;

import java.text.SimpleDateFormat;
import java.util.Set;

import com.yukthi.webutils.common.IWebUtilsCommonConstants;

/**
 * Enumerates list of supported extension field types
 * @author akiran
 */
public enum ExtensionFieldType
{
	/**
	 * Simple string field
	 */
	STRING
	{
		@Override
		public boolean validateValue(String value, SimpleDateFormat dateFormat, Set<String> lovValues)
		{
			return true;
		}
	},
	
	/**
	 * Multi lined string.
	 */
	MULTI_LINE_STRING
	{
		@Override
		public boolean validateValue(String value, SimpleDateFormat dateFormat, Set<String> lovValues)
		{
			return true;
		}
	},
	
	INTEGER
	{
		@Override
		public boolean validateValue(String value, SimpleDateFormat dateFormat, Set<String> lovValues)
		{
			return IWebUtilsCommonConstants.INT_PATTERN.matcher(value).matches();
		}
	},
	
	DECIMAL
	{
		@Override
		public boolean validateValue(String value, SimpleDateFormat dateFormat, Set<String> lovValues)
		{
			return IWebUtilsCommonConstants.DECIMAL_PATTERN.matcher(value).matches() ||
					IWebUtilsCommonConstants.INT_PATTERN.matcher(value).matches();
		}
	},
	
	BOOLEAN
	{
		@Override
		public boolean validateValue(String value, SimpleDateFormat dateFormat, Set<String> lovValues)
		{
			return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
		}
	},
	
	DATE
	{
		@Override
		public boolean validateValue(String value, SimpleDateFormat dateFormat, Set<String> lovValues)
		{
			try
			{
				dateFormat.parse(value);
				return true;
			}catch(Exception ex)
			{
				return false;
			}
		}
	},
	
	LIST_OF_VALUES
	{
		@Override
		public boolean validateValue(String value, SimpleDateFormat dateFormat, Set<String> lovValues)
		{
			return lovValues.contains(value);
		}
	};
	
	public abstract boolean validateValue(String value, SimpleDateFormat dateFormat, Set<String> lovValues);
}
