package com.webutils.services.common;

import com.yukthitech.utils.exceptions.UtilsException;

public class UnauthorizedRequestException extends UtilsException
{
	private static final long serialVersionUID = 1L;
	
	public UnauthorizedRequestException(String message, Object... args)
	{
		super(message, args);
	}
}
