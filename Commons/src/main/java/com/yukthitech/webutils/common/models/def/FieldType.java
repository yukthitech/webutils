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
package com.yukthitech.webutils.common.models.def;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.yukthitech.webutils.common.FileInfo;
import com.yukthitech.webutils.common.ImageInfo;

/**
 * Enumerates list of supported model field types
 * @author akiran
 */
public enum FieldType
{
	/**
	 * Simple string field.
	 */
	STRING("string", String.class, String.class),
	
	/**
	 * Multi lined string.
	 */
	MULTI_LINE_STRING("multiLine", String.class),
	
	/**
	 * Type to be used for password fields.
	 */
	PASSWORD("password", String.class),
	
	/**
	 * Type to be used when formatted html text is expected.
	 */
	HTML("html", String.class),
	
	INTEGER("int", int.class, Integer.class, int.class, Long.class, long.class, Short.class, short.class),
	
	FLOAT("float", float.class, Float.class, float.class, Double.class, double.class),
	
	BOOLEAN("boolean", boolean.class, Boolean.class, boolean.class),
	
	DATE("date", Date.class, Date.class),
	
	LIST_OF_VALUES("lov", long.class),
	
	FILE("file", FileInfo.class, FileInfo.class),
	
	/**
	 * Represent image type file data. 
	 */
	IMAGE("image", ImageInfo.class, ImageInfo.class),
	
	COLOR("color", Color.class)
	;
	
	/**
	 * Mapping from java type to this field type
	 */
	private static Map<Class<?>, FieldType> typeMap;
	
	/**
	 * Name of the current type, used by client to uniquely identify data type
	 */
	private String name;
	
	/**
	 * List of java types that can be mapped to current type
	 */
	private Class<?> javaTypes[];
	
	/**
	 * Defatult java type to be used for this type of fields.
	 */
	private Class<?> defaultJavaType;
	
	/**
	 * Instantiates a new field type.
	 *
	 * @param name Name of the type
	 * @param javaTypes List of java types that can be mapped to current type
	 */
	private FieldType(String name, Class<?> defaultJavaType, Class<?>... javaTypes)
	{
		this.name = name;
		
		if(javaTypes == null || javaTypes.length == 0)
		{
			javaTypes = null;
		}
		
		this.defaultJavaType = defaultJavaType;
		this.javaTypes = javaTypes;
	}
	
	/**
	 * Gets the name of the current type, used by client to uniquely identify data type.
	 *
	 * @return the name of the current type, used by client to uniquely identify data type
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Builds the mapping between java types and this field types
	 * @return
	 */
	private static Map<Class<?>, FieldType> buildTypeMap()
	{
		Map<Class<?>, FieldType> typeMap = new HashMap<>();
		
		for(FieldType fieldType: FieldType.values())
		{
			if(fieldType.javaTypes == null)
			{
				continue;
			}
			
			for(Class<?> jtype: fieldType.javaTypes)
			{
				typeMap.put(jtype, fieldType);
			}
		}
		
		return typeMap;
	}
	
	/**
	 * Fetches field type for the specified java type
	 * @param javaType
	 * @return field type for the specified java type
	 */
	public static FieldType getFieldType(Class<?> javaType)
	{
		if(typeMap == null)
		{
			typeMap = buildTypeMap();
		}

		return typeMap.get(javaType);
	}
	
	/**
	 * Gets the defatult java type to be used for this type of fields.
	 *
	 * @return the defatult java type to be used for this type of fields
	 */
	public Class<?> getDefaultJavaType()
	{
		return defaultJavaType;
	}
}
