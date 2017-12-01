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

package com.yukthitech.webutils.services.dynamic;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.yukthitech.utils.ReflectionUtils;
import com.yukthitech.webutils.annotations.RequestParam;
import com.yukthitech.webutils.common.annotations.ContextAttribute;

/**
 * Factory for dynamic methods
 * @author akiran
 */
public class DynamicMethodFactory
{
	/**
	 * Creates a dynamic method instance using specified type and method. All the parameters should be having annotation indicating
	 * how values for them can be fetched dynamically during invocation. If not, an exception will be thrown.
	 * 
	 * @param type Type in which method is declared
	 * @param method Method for which dynamic method has to be built
	 * @return Dynamic method instance representing specified method
	 */
	public DynamicMethod buildDynamicMethod(Class<?> type, Method method)
	{
		Class<?> paramTypes[] = method.getParameterTypes();
		
		ContextAttribute contextAttribute = null;
		RequestParam requestParam = null;
		List<DynamicArg> dynamicArgs = new ArrayList<>();
		
		//loop through param types
		for(int i = 0; i < paramTypes.length; i++)
		{
			//get dynamic annotations
			contextAttribute = ReflectionUtils.getParameterAnnotation(method, i, ContextAttribute.class);
			requestParam = ReflectionUtils.getParameterAnnotation(method, i, RequestParam.class);
			
			//according to annotation build dynamic arg instance
			if(contextAttribute != null)
			{
				dynamicArgs.add(new DynamicArg(ArgumentType.CONTEXT_PARAM, contextAttribute.value(), paramTypes[i]));
			}
			else if(requestParam != null)
			{
				dynamicArgs.add(new DynamicArg(ArgumentType.REQUEST_PARAM, requestParam.value(), paramTypes[i]));
			}
			//if no dynamic annotation is found throw exception
			else
			{
				throw new DynamicMethodException("Non dynamic parameter found in dynamic method - " + type.getName() + "." + method.getName() + "()");				
			}
		}
		
		//return dynamic method instance
		return new DynamicMethod(type, method, dynamicArgs.toArray(new DynamicArg[0]));
	}
}
