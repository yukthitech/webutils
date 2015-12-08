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

package com.yukthi.webutils.utils;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author akiran
 *
 */
public class BeanProperty
{
	private static Pattern CAP_WORD = Pattern.compile("([A-Z])");
	private String name;
	private String nestedName;
	private String nestedNameParts[];
	
	private Method getter;
	private Method setter;
	
	private Class<?> type;
	
	public BeanProperty(String name, Method getter, Method setter, Class<?> type)
	{
		this.name = name;
		toNestedName(name);
		
		this.getter = getter;
		this.setter = setter;
		
		this.type = type;
	}
	
	private void toNestedName(String name)
	{
		//convert multiple words into nested prop
		StringBuffer res = new StringBuffer();
		Matcher matcher = CAP_WORD.matcher(name);
		boolean matchFound = false;
		
		while(matcher.find())
		{
			matcher.appendReplacement(res, "." + matcher.group(1).toLowerCase());
			matchFound = true;
		}
		
		//if multiple words are not found
		if(!matchFound)
		{
			return;
		}

		matcher.appendTail(res);
		this.nestedName = res.toString();
		this.nestedNameParts = nestedName.split("\\.");
	}

	public String getName()
	{
		return name;
	}
	
	public String getNestedName()
	{
		return nestedName;
	}

	public Method getGetter()
	{
		return getter;
	}

	public Method getSetter()
	{
		return setter;
	}
	
	public Class<?> getType()
	{
		return type;
	}
	
	public String[] getNestedNameParts()
	{
		return nestedNameParts;
	}
}
