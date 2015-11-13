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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;

import com.yukthi.utils.exceptions.InvalidStateException;

/**
 * Common utilities for web applications
 * @author akiran
 */
public class WebUtils
{
	/**
	 * Converts "source" bean into bean of type "targetType" by creating new instance,
	 * and copying properties
	 * @param source Source to be converted
	 * @param targetType Target type to be converted to
	 * @return Converted bean
	 */
	public static <T> T convertBean(Object source, Class<T> targetType)
	{
		try
		{
			T targetBean = targetType.newInstance();
			
			PropertyUtils.copyProperties(targetBean, source);
			
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
	 */
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
}
