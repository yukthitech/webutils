package com.webutils.services.common;

import com.yukthitech.utils.exceptions.UtilsException;

public class ConfgurationException extends UtilsException
{
	private static final long serialVersionUID = 1L;
	
	public ConfgurationException(String message, Object... args)
	{
		super(message, args);
	}
}
