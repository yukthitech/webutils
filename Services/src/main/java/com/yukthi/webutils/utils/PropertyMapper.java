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

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthi.utils.CommonUtils;
import com.yukthi.utils.exceptions.InvalidStateException;

/**
 * Like Apache's {@link PropertyUtils} this class also maps properties from source bean to destination bean. With additional support of 
 * copying properties based on camel case property names. For example, relationId would be mapped to relation.id
 * @author akiran
 */
public class PropertyMapper
{
	private static Logger logger = LogManager.getLogger(PropertyMapper.class);
	
	/**
	 * Cache map which caches the properties of different classes
	 */
	private static Map<Class<?>, Map<String, BeanProperty>> typeToProp = new HashMap<>();
	
	/**
	 * Checks in the cache if the specified bean type property details is already loaded. If loaded returns the same. If not, builds the property map
	 * caches it and returns it.
	 * @param beanType Bean types for which property map needs to be fetched
	 * @return Property details of specified bean type
	 */
	private static synchronized Map<String, BeanProperty> loadProperties(Class<?> beanType)
	{
		Map<String, BeanProperty> nameToProp = typeToProp.get(beanType);
		
		//if type is already loaded return the same
		if(nameToProp != null)
		{
			return nameToProp;
		}
		
		Map<String, BeanProperty> propMap = new HashMap<>();
		PropertyDescriptor propDescLst[] = PropertyUtils.getPropertyDescriptors(beanType);
		BeanProperty beanProperty = null;
		
		//loop through property descriptors and add to bean property map
		for(PropertyDescriptor propDesc : propDescLst)
		{
			beanProperty = new BeanProperty(propDesc.getName(), propDesc.getReadMethod(), propDesc.getWriteMethod(), propDesc.getPropertyType());
			propMap.put(propDesc.getName(), beanProperty);
		}
		
		//cache and return property map
		typeToProp.put(beanType, propMap);
		return propMap;
	}
	
	/**
	 * Copies properties from "source" to "destination". This will be shallow copy. This copy also maps property camel case
	 * naming. For example, relationId would be mapped to relation.id
	 * @param destination Destination to which properties needs to be copied
	 * @param source Source from property values needs to be fetched
	 */
	public static void copyProperties(Object destination, Object source)
	{
		//ensure destination and source are provided
		if(destination == null)
		{
			throw new NullPointerException("Destination can not be null");
		}
		
		if(source == null)
		{
			throw new NullPointerException("Source can not be null");
		}
		
		//load source and destination property maps
		Map<String, BeanProperty> sourcePropertyMap = loadProperties(source.getClass());
		Map<String, BeanProperty> destinationPropertyMap = loadProperties(destination.getClass());
		
		//keep track of simple properties which gets copied directly
		Set<String> sourcePropNames = new HashSet<>(sourcePropertyMap.keySet());
		Set<String> destPropNames = new HashSet<>(destinationPropertyMap.keySet());
		
		BeanProperty sourceProperty = null, destProperty = null;;
		
		//loop through source property and copy all simple (directly matching) properties
		for(String srcProp : sourcePropertyMap.keySet())
		{
			sourceProperty = sourcePropertyMap.get(srcProp);
			destProperty = destinationPropertyMap.get(srcProp);
			
			//if property is not found on destination ignore for now
			if(destProperty == null)
			{
				continue;
			}
			
			//ignore if property is not readable from source or if its not writable on destination
			if(sourceProperty.getGetter() == null || destProperty.getSetter() == null)
			{
				continue;
			}
			
			//if source and destination types are not matching throw error
			if(!CommonUtils.isAssignable(sourceProperty.getType(), destProperty.getType()))
			{
				throw new InvalidStateException("For property '{}' source and destination data types are not matching "
						+ "[Source type : {}, Source Property Type: {}, Desctination Type: {}, Destination Property Type: {}] ", 
						srcProp, source.getClass().getName(), sourceProperty.getType().getName(), 
						destination.getClass().getName(), destProperty.getType().getName());
			}
			
			//copy property from source to destination
			try
			{
				PropertyUtils.setProperty(destination, srcProp, PropertyUtils.getProperty(source, srcProp));
			}catch(Exception ex)
			{
				throw new InvalidStateException(ex, "An error occurred while copyng property '{}' from source type '{}' to destination type '{}'", 
						srcProp, source.getClass().getName(), destination.getClass().getName());
			}
			
			//on success copy remove from below collections, so at end they contain only complex properties
			sourcePropNames.remove(srcProp);
			destPropNames.remove(srcProp);
		}
		
		Object value = null;
		
		//loop through complex properties from source
		for(String propName : sourcePropNames)
		{
			sourceProperty = sourcePropertyMap.get(propName);
			
			//if the camel case of source property name does not indicate nesting
			if(sourceProperty.getNestedName() == null)
			{
				continue;
			}
			
			try
			{
				//fetch value from source
				value = PropertyUtils.getProperty(source, propName);
				
				if(value == null)
				{
					continue;
				}

				//try to set value on destination by creating all required beans on the path
				if(!setNestedProperty(destination, sourceProperty.getNestedName(), sourceProperty.getNestedNameParts(), value))
				{
					logger.trace("Failed to map source {}'s property '{}'. Tried to map to destination property '{}' but found it non-existing/read-only on destination type - {}", 
							source.getClass().getName(), propName, sourceProperty.getNestedName(), destination.getClass().getName());
				}
			}catch(Exception ex)
			{
				logger.trace("Failed to map source {}'s property '{}'. Tried to map to destination {}'s nested property '{}' but got error - {}", 
						source.getClass().getName(), propName, destination.getClass().getName(), sourceProperty.getNestedName(), ex);
			}
		}
	
		//loop through complex properties from destination
		for(String propName : destPropNames)
		{
			destProperty = destinationPropertyMap.get(propName);
			
			//if destination camel case does not indicate nesting, ignore
			if(destProperty.getNestedName() == null)
			{
				continue;
			}
			
			try
			{
				//get value from source using indicated nested property name
				value = PropertyUtils.getProperty(source, destProperty.getNestedName());
				
				if(value == null)
				{
					continue;
				}
				
				//if value is obtained, set on destination
				PropertyUtils.setProperty(destination, propName, value);
			}catch(Exception ex)
			{
				logger.debug("Failed to set value of destination {}'s property '{}'. Tried to fetch nested property '{}' from source '{}' but got error - {}", 
						destination.getClass().getName(), propName, destProperty.getNestedName(), source.getClass().getName(), ex);
			}
		}
	}
	
	/**
	 * Sets specified value on destination using specified nested property path by creating all the required intermediate beans
	 * @param destination Destination on which value needs to be set
	 * @param nestedProp Nested property name, used only for messaging
	 * @param nestedPropPath Nested property path using which value will be set
	 * @param value Value to be set
	 * @return true only if nested property path is found on destination path. If not false.
	 */
	private static boolean setNestedProperty(Object destination, String nestedProp, String nestedPropPath[], Object value)
	{
		PropertyDescriptor propDesc = null;
		
		int maxIdx = nestedPropPath.length - 1;
		Object currentPropValue = null, prevPropValue = destination;
		
		//loop through property path
		for(int i = 0; i <= maxIdx; i++)
		{
			try
			{
				//get intermediate property descriptor
				try
				{
					propDesc = PropertyUtils.getPropertyDescriptor(prevPropValue, nestedPropPath[i]);
				}catch(Exception ex)
				{}
				
				//if the property is not found or found as read only, return false
				if(propDesc == null || propDesc.getWriteMethod() == null)
				{
					return false;
				}
				
				//if end of path is reached, set the final value and break the loop
				if(i == maxIdx)
				{
					PropertyUtils.setProperty(prevPropValue, nestedPropPath[i], value);
					break;
				}

				//if read method is present, check if the intermediate bean is already present
				if(propDesc.getReadMethod() != null)
				{
					currentPropValue = PropertyUtils.getProperty(prevPropValue, nestedPropPath[i]);
				}
				//if read method is not present, assume value as null
				else
				{
					currentPropValue = null;
				}
				
				//if current value is not null, go to next level in path
				if(currentPropValue != null)
				{
					prevPropValue = currentPropValue;
					continue;
				}
				
				//create intermediate bean and set it on the path (on current property)
				currentPropValue = propDesc.getPropertyType().newInstance();
				PropertyUtils.setProperty(prevPropValue, nestedPropPath[i], currentPropValue);
				
				prevPropValue = currentPropValue;
			}catch(Exception ex)
			{
				throw new InvalidStateException(ex, "An error occurred while setting nested property - {}", nestedProp);
			}
		}
		
		return true;
	}
}
