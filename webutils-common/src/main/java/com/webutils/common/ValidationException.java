package com.webutils.common;

import com.yukthitech.utils.exceptions.UtilsException;

public class ValidationException extends UtilsException
{
	private static final long serialVersionUID = 1L;

	public ValidationException(String message, Object... args)
	{
		super(message, args);
	}
}
