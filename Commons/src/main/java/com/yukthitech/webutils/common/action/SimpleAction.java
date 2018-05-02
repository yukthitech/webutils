package com.yukthitech.webutils.common.action;

/**
 * Simple action which would simply send event to server.
 *  
 * @author akiran
 */
public class SimpleAction implements IAgentAction
{
	/**
	 * Label to be used for action.
	 */
	private String label;

	/**
	 * Event to be generated once the form is submitted.
	 */
	private String event;

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
