package com.yukthitech.webutils.common.action;

/**
 * Action to be executed to fail the current action plan.
 * 
 * @author akiran
 */
public class FailAction extends AbstractAgentAction implements IAgentAction
{
	/**
	 * Message to be included in error.
	 */
	private String message;

	/**
	 * Instantiates a new simple action.
	 */
	public FailAction()
	{}

	/**
	 * Instantiates a new simple action.
	 *
	 * @param name
	 *            the name
	 * @param label
	 *            the label
	 */
	public FailAction(String name, String label)
	{
		super(name, label);
	}

	/**
	 * Gets the message to be included in error.
	 *
	 * @return the message to be included in error
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * Sets the message to be included in error.
	 *
	 * @param message the new message to be included in error
	 */
	public void setMessage(String message)
	{
		this.message = message.trim();
	}
}
