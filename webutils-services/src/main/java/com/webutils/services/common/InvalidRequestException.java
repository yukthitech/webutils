package com.webutils.services.common;

import com.yukthitech.utils.exceptions.UtilsException;

public class InvalidRequestException extends UtilsException
{
	private static final long serialVersionUID = 1L;
	
	public InvalidRequestException(String message, Object... args)
	{
		super(message, args);
	}
}
