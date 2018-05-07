package com.yukthitech.webutils.common.action;

import java.util.List;

/**
 * This action will enable to make a call and on completion of call
 * displays the form. And on submission of form specified event will be generated.
 * @author akiran
 */
public class CallAndFillFormAction implements IAgentAction
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
	 * Number to be called.
	 */
	private String number;
	
	/**
	 * Name of the model class based on which dynamic form needs to be displayed.
	 */
	private String modelName;
	
	/**
	 * Event to be generated once the form is submitted.
	 */
	private String event;
	
	/**
	 * List of action buttons which user can click once form is filled.
	 */
	private List<String> actionItems;

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
	 * Gets the number to be called.
	 *
	 * @return the number to be called
	 */
	public String getNumber()
	{
		return number;
	}

	/**
	 * Sets the number to be called.
	 *
	 * @param number the new number to be called
	 */
	public void setNumber(String number)
	{
		this.number = number;
	}

	/**
	 * Gets the name of the model class based on which dynamic form needs to be displayed.
	 *
	 * @return the name of the model class based on which dynamic form needs to be displayed
	 */
	public String getModelName()
	{
		return modelName;
	}

	/**
	 * Sets the name of the model class based on which dynamic form needs to be displayed.
	 *
	 * @param modelName the new name of the model class based on which dynamic form needs to be displayed
	 */
	public void setModelName(String modelName)
	{
		this.modelName = modelName;
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

	/**
	 * Gets the list of action buttons which user can click once form is filled.
	 *
	 * @return the list of action buttons which user can click once form is filled
	 */
	public List<String> getActionItems()
	{
		return actionItems;
	}

	/**
	 * Sets the list of action buttons which user can click once form is filled.
	 *
	 * @param actionItems the new list of action buttons which user can click once form is filled
	 */
	public void setActionItems(List<String> actionItems)
	{
		this.actionItems = actionItems;
	}
}
