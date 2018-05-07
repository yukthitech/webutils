package com.yukthitech.webutils.common.action;

/**
 * Simple action which would simply send event to server.
 *  
 * @author akiran
 */
public class SimpleAction implements IAgentAction
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
	 * Event to be generated once the form is submitted.
	 */
	private String event;
	
	/**
	 * Instantiates a new simple action.
	 */
	public SimpleAction()
	{}
	
	/**
	 * Instantiates a new simple action.
	 *
	 * @param name the name
	 * @param label the label
	 */
	public SimpleAction(String name, String label)
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
	 * Gets the event to be generated once the form is submitted.
	 *
	 * @return the event to be generated once the form is submitted
	 */
	public String getEvent()
	{
		return event;
	}

	/**
	 * Sets the event to be generated once the form is submitted.
	 *
	 * @param event the new event to be generated once the form is submitted
	 */
	public void setEvent(String event)
	{
		this.event = event;
	}
}
