package com.yukthitech.webutils.client.actionplan.executor;

/**
 * Event object used to indicate callback that a particular action is completed.
 * @author akiran
 */
public class ActionCompletedEvent
{
	/**
	 * Step under which action is completed.
	 */
	private String step;
	
	/**
	 * Action completed.
	 */
	private String action;

	/**
	 * Instantiates a new action completed event.
	 *
	 * @param step the step
	 * @param action the action
	 */
	public ActionCompletedEvent(String step, String action)
	{
		this.step = step;
		this.action = action;
	}

	/**
	 * Gets the step under which action is completed.
	 *
	 * @return the step under which action is completed
	 */
	public String getStep()
	{
		return step;
	}

	/**
	 * Gets the action completed.
	 *
	 * @return the action completed
	 */
	public String getAction()
	{
		return action;
	}
}
