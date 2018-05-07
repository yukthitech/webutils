package com.yukthitech.webutils.common.action;

import java.util.ArrayList;
import java.util.List;

/**
 * Action which displays the specified form, which user can fill. And after submission of 
 * form, specified event will be generated.
 * @author akiran
 */
public class FillFormAction implements IAgentAction
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
	 * Name of the model class based on which dynamic form needs to be displayed.
	 */
	private String modelName;
	
	/**
	 * Event to be generated once the form is submitted.
	 */
	private String event;
	
	/**
	 * Title to be used for the form (on the top).
	 */
	private String title;
	
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
	
	/**
	 * Adds the specified item to this fill-form action.
	 * @param item item to add
	 */
	public void addActionItem(String item)
	{
		if(this.actionItems == null)
		{
			this.actionItems = new ArrayList<>();
		}
		
		this.actionItems.add(item);
	}

	/**
	 * Gets the title to be used for the form (on the top).
	 *
	 * @return the title to be used for the form (on the top)
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * Sets the title to be used for the form (on the top).
	 *
	 * @param title the new title to be used for the form (on the top)
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}
}
