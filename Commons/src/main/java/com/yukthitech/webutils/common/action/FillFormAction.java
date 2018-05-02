package com.yukthitech.webutils.common.action;

import java.util.List;

/**
 * Action which displays the specified form, which user can fill. And after submission of 
 * form, specified event will be generated.
 * @author akiran
 */
public class FillFormAction implements IAgentAction
{
	/**
	 * Label to be used for action.
	 */
	private String label;

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
