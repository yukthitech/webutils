package com.yukthitech.webutils.common.action;

/**
 * Action to be executed to display alert.
 *  
 * @author akiran
 */
public class AlertAction extends AbstractAgentAction implements IAgentAction
{
	/**
	 * Message to be displayed.
	 */
	private String message;
	
	/**
	 * Executes next action if true. if not next actions will not be executed.
	 * Useful for displaying errors.
	 */
	private boolean executeNextAction = true;
	
	/**
	 * Instantiates a new simple action.
	 */
	public AlertAction()
	{}
	
	/**
	 * Instantiates a new simple action.
	 *
	 * @param name the name
	 * @param label the label
	 */
	public AlertAction(String name, String label)
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

	/**
	 * Checks if is executes next action if true. if not next actions will not be executed. Useful for displaying errors.
	 *
	 * @return the executes next action if true
	 */
	public boolean isExecuteNextAction()
	{
		return executeNextAction;
	}

	/**
	 * Sets the executes next action if true. if not next actions will not be executed. Useful for displaying errors.
	 *
	 * @param executeNextAction the new executes next action if true
	 */
	public void setExecuteNextAction(boolean executeNextAction)
	{
		this.executeNextAction = executeNextAction;
	}
}
