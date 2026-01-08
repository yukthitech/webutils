package com.webutils.services.common;

import com.yukthitech.utils.exceptions.UtilsException;

public class UnauthenticatedRequestException extends UtilsException
{
	private static final long serialVersionUID = 1L;
	
	public UnauthenticatedRequestException(String message, Object... args)
	{
		super(message, args);
	}
}
