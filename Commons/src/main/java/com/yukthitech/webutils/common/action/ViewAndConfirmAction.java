package com.yukthitech.webutils.common.action;

import java.util.ArrayList;
import java.util.List;

/**
 * This action is expected to display the embedded html content which user
 * can read and confirm to proceed further or not. 
 * @author akiran
 */
public class ViewAndConfirmAction extends AbstractAgentAction implements IAgentAction
{
	/**
	 * Content to be displayed to the user.
	 */
	private String htmlContent;
	
	/**
	 * Event to be generated once the form is submitted.
	 */
	private String event;
	
	/**
	 * List of action buttons which user can click once form is filled.
	 */
	private List<String> actionItems;

	/**
	 * Gets the content to be displayed to the user.
	 *
	 * @return the content to be displayed to the user
	 */
	public String getHtmlContent()
	{
		return htmlContent;
	}

	/**
	 * Sets the content to be displayed to the user.
	 *
	 * @param htmlContent the new content to be displayed to the user
	 */
	public void setHtmlContent(String htmlContent)
	{
		this.htmlContent = htmlContent;
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
	 * Adds the action item.
	 *
	 * @param item the item
	 */
	public void addActionItem(String item)
	{
		if(this.actionItems == null)
		{
			this.actionItems = new ArrayList<>();
		}
		
		this.actionItems.add(item);
	}
}
