package com.webutils.common;

import java.lang.reflect.Method;

import com.yukthitech.utils.exceptions.InvalidStateException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceMethod
{
	private Object service;
	
	private Method method;
	
	public Object invoke()
	{
		try
		{
			return method.invoke(service);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while invoking service method: {}.{}()", method.getDeclaringClass().getName(), method.getName(), ex);
		}
	}
}
