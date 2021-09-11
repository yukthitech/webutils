package com.yukthitech.webutils.services;

import com.yukthitech.utils.exceptions.UtilsException;

/**
 * Exception to be thrown when target repository is not found.
 * @author akiran
 */
public class NoRepositoryFoundException extends UtilsException
{
	private static final long serialVersionUID = 1L;

	public NoRepositoryFoundException(String message, Object... args)
	{
		super(message, args);
	}
}
