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

package com.yukthitech.webutils.utils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.yukthitech.webutils.InvalidRequestException;
import com.yukthitech.webutils.repository.WebutilsBaseEntity;

/**
 * Common utilities for web applications
 * @author akiran
 */
public class WebUtils
{
	private static Map<Class<?>, Map<Integer, Object>> enumOrdinalCache = new HashMap<>();
	
	/**
	 * Converts "source" bean into bean of type "targetType" by creating new instance,
	 * and copying properties
	 * @param source Source to be converted
	 * @param targetType Target type to be converted to
	 * @return Converted bean
	 * /
	public static <T> T convertBean(Object source, Class<T> targetType)
	{
		if(source == null)
		{
			return null;
		}
		
		try
		{
			T targetBean = targetType.newInstance();
			
			//PropertyUtils.copyProperties(targetBean, source);
			PropertyMapper.copyProperties(targetBean, source);
			
			return targetBean;
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while converting {} bean to {} bean", source.getClass().getName(), targetType.getName());
		}
	}
	
	/**
	 * Converts specified beans into target type collection by creating target instance and 
	 * copying source bean properties
	 * @param sourceLst Source bean list to be converted
	 * @param targetType Target bean type
	 * @return Converted bean list
	 * /
	public static <T> List<T> convertBeans(Collection<?> sourceLst, Class<T> targetType)
	{
		if(CollectionUtils.isEmpty(sourceLst))
		{
			return Collections.emptyList();
		}
		
		ArrayList<T> targetLst = new ArrayList<>(sourceLst.size());
		T target = null;
		
		for(Object source : sourceLst)
		{
			target = convertBean(source, targetType);
			targetLst.add(target);
		}
		
		return targetLst;
	}
	
	/**
	 * Fetches current time in mins
	 * @return current time in mins
	 */
	public static long currentTimeInMin()
	{
		return System.currentTimeMillis() / 60000L;
	}
	
	/**
	 * Converts set of enums into set of integers based on their ordinals 
	 * @param enums Enums to converted
	 * @return Set of input enum ordinals
	 */
	public static Set<Integer> toEnumOrdinals(Set<? extends Enum<?>> enums)
	{
		//if enums is null return null
		if(enums == null)
		{
			return null;
		}
		
		//create result set
		Set<Integer> ordinalSet = new HashSet<>();
		
		//convert enum to int
		for(Enum<?> e: enums)
		{
			ordinalSet.add(e.ordinal());
		}
		
		return ordinalSet;
	}
	
	/**
	 * Converts set of enum ordinals into set of enums
	 * @param enumOrdinals Ordinals to convert
	 * @param enumType Enum type to which conversion should happen
	 * @return Converted enums
	 */
	@SuppressWarnings("unchecked")
	public static <E extends Enum<E>> Set<E> toEnums(Set<Integer> enumOrdinals, Class<E> enumType)
	{
		//if ordinals is null
		if(enumOrdinals == null)
		{
			return null;
		}
		
		//get enum map from cache
		Map<Integer, Object> enumMap = enumOrdinalCache.get(enumType);
		
		//if not present in cache, create one and add it to cache
		if(enumMap == null)
		{
			enumMap = new HashMap<>();
			E enumConstants[] = enumType.getEnumConstants();
			
			for(E e : enumConstants)
			{
				enumMap.put(e.ordinal(), e);
			}
			
			enumOrdinalCache.put(enumType, enumMap);
		}
		
		//convert ordinals into enum based on enum map
		Set<E> enums = new HashSet<>();
		
		for(Integer ordinal : enumOrdinals)
		{
			enums.add((E)enumMap.get(ordinal));
		}
		
		return enums;
	}

	/**
	 * Converts specified method into string
	 * @param method Method to convert
	 * @return Converted string
	 */
	public static String toString(Method method)
	{
		return method.getDeclaringClass().getName() + "." + method.getName() + "()";
	}
	
	/**
	 * Validates if the required parameters are set on entity during update operation.
	 * @param entity Entity to be validated for update
	 */
	public static void validateEntityForUpdate(WebutilsBaseEntity entity)
	{
		//if invalid id is specified
		if(entity.getId() == null || entity.getId() <= 0)
		{
			throw new InvalidRequestException("No id specified for update");
		}

		//if invalid version is specified
		if(entity.getVersion() == null || entity.getVersion() <= 0)
		{
			throw new InvalidRequestException("No version specified for update");
		}
	}
}
