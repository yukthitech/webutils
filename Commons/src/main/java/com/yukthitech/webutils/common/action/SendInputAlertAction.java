package com.yukthitech.webutils.common.action;

/**
 * Action for accepting message as input and then sending it as part of alert.
 */
public class SendInputAlertAction extends SendAlertAction
{
	/**
	 * Title to be used to accept user input.
	 */
	private String title;
	
	/**
	 * Gets the title to be used to accept user input.
	 *
	 * @return the title to be used to accept user input
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * Sets the title to be used to accept user input.
	 *
	 * @param title the new title to be used to accept user input
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}
} 