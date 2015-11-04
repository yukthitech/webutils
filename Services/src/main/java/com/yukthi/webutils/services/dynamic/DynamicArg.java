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

package com.yukthi.webutils.services.dynamic;

/**
 * Represents parameter details for dynamic method
 * @author akiran
 */
public class DynamicArg
{
	/**
	 * Type of the parameter
	 */
	private ArgumentType argumentType;
	
	/**
	 * Name of the attribute using which value should be obtained
	 */
	private String name;
	
	/**
	 * Java field type
	 */
	private Class<?> fieldType;

	/**
	 * Instantiates a new dynamic param.
	 *
	 * @param type the type
	 * @param name the name
	 * @param fieldType Field java type
	 */
	public DynamicArg(ArgumentType type, String name, Class<?> fieldType)
	{
		this.argumentType = type;
		this.name = name;
		this.fieldType = fieldType;
	}

	/**
	 * Gets the type of the parameter.
	 *
	 * @return the type of the parameter
	 */
	public ArgumentType getArgumentType()
	{
		return argumentType;
	}

	/**
	 * Sets the type of the parameter.
	 *
	 * @param argumentType the new type of the parameter
	 */
	public void setArgumentType(ArgumentType argumentType)
	{
		this.argumentType = argumentType;
	}

	/**
	 * Gets the name of the attribute using which value should be obtained.
	 *
	 * @return the name of the attribute using which value should be obtained
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the attribute using which value should be obtained.
	 *
	 * @param name the new name of the attribute using which value should be obtained
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the java field type.
	 *
	 * @return the java field type
	 */
	public Class<?> getFieldType()
	{
		return fieldType;
	}

	/**
	 * Sets the java field type.
	 *
	 * @param fieldType the new java field type
	 */
	public void setFieldType(Class<?> fieldType)
	{
		this.fieldType = fieldType;
	}
	
	
}
