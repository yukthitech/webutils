package com.yukthitech.webutils.common.action;

/**
 * Action to be executed to accept single input from user.
 *  
 * @author akiran
 */
public class InputAction implements IAgentAction
{
	/**
	 * Name of the action to be sent back if this action is performed.
	 */
	private String name;
	
	/**
	 * Label to be used for action.
	 */
	private String label;

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
		this.name = name;
		this.label = label;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.webutils.common.action.IAgentAction#getName()
	 */
	@Override
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the action to be sent back if this action is performed.
	 *
	 * @param name the new name of the action to be sent back if this action is performed
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the label to be used for action.
	 *
	 * @return the label to be used for action
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * Sets the label to be used for action.
	 *
	 * @param label the new label to be used for action
	 */
	public void setLabel(String label)
	{
		this.label = label;
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
