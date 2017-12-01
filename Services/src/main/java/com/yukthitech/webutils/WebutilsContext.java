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

package com.yukthitech.webutils;

import java.util.HashMap;
import java.util.Map;

/**
 * This is thread-local based on context. Which is used to maintain data specific to request which gets initialized
 * and destroyed by a filter/spring-interceptor
 * @author akiran
 */
public class WebutilsContext
{
	/**
	 * Thread local object used to maintain thread specific web utils context
	 */
	private static ThreadLocal<WebutilsContext> threadLocal = new ThreadLocal<>();
	
	private Map<String, Object> attributeMap = new HashMap<>();
	
	/**
	 * Fetches WebUtils context specific to current thread
	 * @return WebUtils context specific to current thread
	 */
	public synchronized static WebutilsContext getContext()
	{
		//get current context
		WebutilsContext context = threadLocal.get();
		
		//if not found create one and set it on thread local
		if(context == null)
		{
			context = new WebutilsContext();
			threadLocal.set(context);
		}
		
		return context;
	}
	
	/**
	 * Adds attribute object with specified name
	 * @param name Name of the attribute
	 * @param object Attribute object to be set
	 * @return Current webutils context
	 */
	public WebutilsContext addAttribute(String name, Object object)
	{
		attributeMap.put(name, object);
		return this;
	}
	
	/**
	 * Fetches the attribute with specified name
	 * @param name Name of the attribute 
	 * @return Matching attribute object
	 */
	public Object getAttribute(String name)
	{
		return attributeMap.get(name);
	}
	
	/**
	 * Fetches attribute map of the context
	 * @return
	 */
	public Map<String, Object> getAttributeMap() 
	{
		return attributeMap;
	}
}
