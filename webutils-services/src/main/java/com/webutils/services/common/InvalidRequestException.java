package com.webutils.services.common;

import java.util.HashMap;
import java.util.Map;

import com.yukthitech.utils.exceptions.UtilsException;

public class InvalidRequestException extends UtilsException
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Extra parameters that can be communicated with client.
	 */
	private Map<String, Object> parameters;
	
	public InvalidRequestException(String message, Object... args)
	{
		super(message, args);
	}
	
	public InvalidRequestException setParameters(Map<String, Object> parameters)
	{
		this.parameters = parameters;
		return this;
	}
	
	public InvalidRequestException addParameter(String name, Object value)
	{
		if(this.parameters == null)
		{
			this.parameters = new HashMap<String, Object>();
		}
		
		this.parameters.put(name, value);
		return this;
	}
	
	public Map<String, Object> getParameters()
	{
		return parameters;
	}
}
