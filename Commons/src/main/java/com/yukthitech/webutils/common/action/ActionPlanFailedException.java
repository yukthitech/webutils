package com.yukthitech.webutils.common.action;

/**
 * Used to fail action plan.
 * @author akiran
 */
public class ActionPlanFailedException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new action plan failed exception.
	 *
	 * @param message the message
	 */
	public ActionPlanFailedException(String message)
	{
		super(message);
	}
}
