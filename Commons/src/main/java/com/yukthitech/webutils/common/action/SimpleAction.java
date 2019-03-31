package com.yukthitech.webutils.common.action;

/**
 * Simple action which would simply send event to server.
 *  
 * @author akiran
 */
public class SimpleAction extends AbstractAgentAction implements IAgentAction
{
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
		super(name, label);
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
