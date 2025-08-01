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
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.ConvertUtils;
import com.yukthitech.utils.PropertyAccessor;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.webutils.WebutilsContext;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Represents method that has only dynamic arguments or no arguments. Values for
 * these parameters are obtained dynamically
 * 
 * @author akiran
 */
public class DynamicMethod
{
	private static Logger logger = LogManager.getLogger(DynamicMethod.class);
	
	/**
	 * Class in which this dynamic method is defined
	 */
	private Class<?> type;

	/**
	 * Java method representing defining dynamic behavior
	 */
	private Method method;

	/**
	 * Argument details for the dynamic method
	 */
	private DynamicArg arguments[];
	
	/**
	 * Default object to be used while method invocation
	 */
	private Object defaultObject;

	/** The request. */
	@Autowired
	private HttpServletRequest request;

	/**
	 * Instantiates a new dynamic method.
	 *
	 * @param type the type
	 * @param method the method
	 * @param arguments the arguments
	 */
	public DynamicMethod(Class<?> type, Method method, DynamicArg[] arguments)
	{
		this.type = type;
		this.method = method;
		this.arguments = arguments;
	}
	
	public Object getDefaultObject()
	{
		return defaultObject;
	}



	public void setDefaultObject(Object defaultObject)
	{
		this.defaultObject = defaultObject;
	}



	/**
	 * Gets the class in which this dynamic method is defined.
	 *
	 * @return the class in which this dynamic method is defined
	 */
	public Class<?> getType()
	{
		return type;
	}

	/**
	 * Gets the java method representing defining dynamic behavior.
	 *
	 * @return the java method representing defining dynamic behavior
	 */
	public Method getMethod()
	{
		return method;
	}

	/**
	 * Gets the argument details for the dynamic method.
	 *
	 * @return the argument details for the dynamic method
	 */
	public DynamicArg[] getArguments()
	{
		return arguments;
	}

	/**
	 * Invokes the dynamic method and passing dynamic values.
	 * @param object Object on which dynamic method needs to be invoked
	 * @return Result of the dynamic method invocation
	 */
	public Object invoke(Object object)
	{
		if(object == null)
		{
			throw new NullPointerException("No object specified for dynamic method invocation");
		}
		
		WebutilsContext context = WebutilsContext.getContext();
		Object argValues[] = null;
		
		//if dynamic arguments are present
		if(arguments != null)
		{
			argValues = new Object[arguments.length];
			
			//loop through argument details
			for(int i = 0; i < this.arguments.length; i++)
			{
				//based on argument type fetch the value
				if(arguments[i].getArgumentType() == ArgumentType.CONTEXT_PARAM)
				{
					try
					{
						argValues[i] = PropertyAccessor.getProperty(context.getAttributeMap(), arguments[i].getName());
					}catch(Exception ex)
					{
						throw new InvalidStateException("An error occurred while fetching context attribute - {}", arguments[i].getName(), ex);
					}
				}
				else
				{
					argValues[i] = request.getParameter(arguments[i].getName());
				}
				
				//convert to the target type
				if(argValues[i] != null)
				{
					argValues[i] = ConvertUtils.convert(argValues[i], this.arguments[i].getFieldType());
				}
				//if value is null, get default value
				else
				{
					argValues[i] = CommonUtils.getDefaultValue(this.arguments[i].getFieldType());
				}
			}
		}
		
		//method to be invoked
		try
		{
			logger.debug("Invoking method {}.{}() with arguments - {}", type.getName(), method.getName(), Arrays.toString(argValues));
			return method.invoke(object, argValues);
		}catch(Exception ex)
		{
			throw new IllegalStateException("An exception occurred while invoking dynamic method - " + this, ex);
		}
	}
	
	/**
	 * Invokes the dynamic method using default object set on this instance
	 * @return Result of dynamic method invocation
	 */
	public Object invoke()
	{
		return invoke(defaultObject);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append(type.getName()).append(".").append(method.getName()).append("()");
		builder.append("]");
		return builder.toString();
	}

}
