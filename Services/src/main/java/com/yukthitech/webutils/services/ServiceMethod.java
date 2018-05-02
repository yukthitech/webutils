package com.yukthitech.webutils.services;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Service method combination.
 * @author akiran
 */
public class ServiceMethod
{
	/**
	 * Service enclosing the method.
	 */
	private Object service;
	
	/**
	 * Service method.
	 */
	private Method method;

	/**
	 * Instantiates a new service method.
	 *
	 * @param service the service
	 * @param method the method
	 */
	public ServiceMethod(Object service, Method method)
	{
		this.service = service;
		this.method = method;
	}
	
	/**
	 * Gets the service enclosing the method.
	 *
	 * @return the service enclosing the method
	 */
	public Object getService()
	{
		return service;
	}
	
	/**
	 * Gets the service method.
	 *
	 * @return the service method
	 */
	public Method getMethod()
	{
		return method;
	}
	
	/**
	 * Invokes underlying service method with specified arguments.
	 * @param args args for method invocation
	 * @return method result
	 */
	public Object invoke(Object... args) throws IllegalAccessException, InvocationTargetException
	{
		return method.invoke(service, args);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return method.getDeclaringClass().getName() + "." + method.getName() + "()";
	}
}
