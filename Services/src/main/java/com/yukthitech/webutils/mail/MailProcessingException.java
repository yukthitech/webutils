package com.yukthitech.webutils.mail;

import com.yukthitech.utils.exceptions.UtilsCheckedException;

/**
 * Mail processing exception to be thrown when there is an error in processing the mail.
 * @author akiran
 */
public class MailProcessingException extends UtilsCheckedException
{
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new mail processing exception.
	 *
	 * @param message the message
	 * @param args the args
	 */
	public MailProcessingException(String message, Object... args)
	{
		super(message, args);
	}

	/**
	 * Instantiates a new mail processing exception.
	 *
	 * @param cause the cause
	 * @param message the message
	 * @param args the args
	 */
	public MailProcessingException(Throwable cause, String message, Object... args)
	{
		super(cause, message, args);
	}
}
