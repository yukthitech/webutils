package com.yukthitech.webutils.common.action;

/**
 * Action to be executed to accept single input from user.
 *  
 * @author akiran
 */
public class InputAction extends AbstractAgentAction implements IAgentAction
{
	/**
	 * Message to be displayed.
	 */
	private String message;
	
	/**
	 * Instantiates a new simple action.
	 */
	public InputAction()
	{}
	
	/**
	 * Instantiates a new simple action.
	 *
	 * @param name the name
	 * @param label the label
	 */
	public InputAction(String name, String label)
	{
		super(name, label);
	}

	/**
	 * Gets the message to be displayed.
	 *
	 * @return the message to be displayed
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * Sets the message to be displayed.
	 *
	 * @param message the new message to be displayed
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}
}
