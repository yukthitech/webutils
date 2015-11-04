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

package com.yukthi.webutils.security;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.yukthi.utils.ConvertUtils;
import com.yukthi.utils.exceptions.InvalidArgumentException;
import com.yukthi.utils.exceptions.InvalidStateException;
import com.yukthi.webutils.annotations.SecurityField;

/**
 * Wrapper over user details to access security fields with ease
 * @author akiran
 */
public class UserDetailsWrapper
{
	/**
	 * Type of expected user details
	 */
	private Class<?> userDetailsType;
	
	/**
	 * Security fields from user details
	 */
	private Map<String, Field> securityFields = new TreeMap<>();
	
	/**
	 * Creates wrapper for specified type
	 * @param userDetailsType User details type
	 */
	public UserDetailsWrapper(Class<?> userDetailsType)
	{
		this.userDetailsType = userDetailsType;
		
		Field fields[] = userDetailsType.getDeclaredFields();
		
		//loop through the fields
		for(Field field : fields)
		{
			//if field is not security field, ignore
			if(field.getAnnotation(SecurityField.class) == null)
			{
				continue;
			}
			
			//if non primitive field is marked as security field, throw error
			if(!field.getType().isPrimitive())
			{
				throw new InvalidStateException("Non primitve field '{}' is marked as @SecurityField in user-details - {}", field.getName(), userDetailsType.getName());
			}
			
			field.setAccessible(true);
			securityFields.put(field.getName(), field);
		}
	}
	
	/**
	 * Fetches the values of all security fields from specified user details as map
	 * @param userDetails User details for which security details needs t be fetched
	 * @return Field values as list
	 */
	public String[] getSecurityFields(UserDetails<?> userDetails)
	{
		//if user details type is different then expected one
		if(!this.userDetailsType.isAssignableFrom(userDetails.getClass()))
		{
			throw new InvalidArgumentException("Invalid user details {} specified. Expected type - {}", userDetails.getClass().getName(), this.userDetailsType.getName());
		}
		
		//if no security fields are defined
		if(securityFields.isEmpty())
		{
			return new String[0];
		}
		
		//fetch all security field values and build map
		List<String> values = new ArrayList<>();
		
		for(Field field : securityFields.values())
		{
			try
			{
				values.add("" + field.get(userDetails));
			}catch(Exception ex)
			{
				throw new IllegalStateException("An error occurred while fetching field value - " + field.getName(), ex);
			}
		}
		
		return values.toArray(new String[0]);
	}
	
	/**
	 * Sets the security field values on the specified user details
	 * @param userDetails User details on which security details needs to be set
	 * @param values Security field values
	 */
	public void setSecurityFields(UserDetails<?> userDetails, String values[])
	{
		if(!this.userDetailsType.isAssignableFrom(userDetails.getClass()))
		{
			throw new InvalidArgumentException("Invalid user details {} specified. Expected type - {}", userDetails.getClass().getName(), this.userDetailsType.getName());
		}
		
		if(securityFields.isEmpty())
		{
			return;
		}
		
		if(securityFields.size() != values.length)
		{
			throw new InvalidArgumentException("Invalid number of field values specified - {}. Expected - {}", values.length, securityFields.size());
		}
		
		Object value = null;
		int idx = 0;
		
		//set all security field value from map
		for(Field field : securityFields.values())
		{
			try
			{
				value = ConvertUtils.convert(values[idx], field.getType());
				field.set(userDetails, value);
				idx++;
			}catch(Exception ex)
			{
				throw new IllegalStateException("An error occurred while fetching field value - " + field.getName(), ex);
			}
		}
	}
	
	/**
	 * Creates new instance of underlying user details type
	 * @return new user details instance
	 */
	public UserDetails<?> newDetails()
	{
		try
		{
			return (UserDetails<?>)userDetailsType.newInstance();
		}catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while creating user details instance - " + userDetailsType.getName(), ex);
		}
	}
}
