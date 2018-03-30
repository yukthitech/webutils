package com.yukthitech.webutils.common.alerts;

import com.yukthitech.webutils.common.FileInfo;
import com.yukthitech.webutils.common.annotations.IgnoreField;
import com.yukthitech.webutils.common.annotations.Model;

/**
 * Represents an alert.
 * @author akiran
 */
@Model
public class AlertDetails
{
	/**
	 * Id of the alert.
	 */
	private long id;
	
	/**
	 * Source which is generating this alert.
	 */
	private String source;
	
	/**
	 * Title of the alert.
	 */
	private String title;
	
	/**
	 * Message to be sent.
	 */
	private String message;
	
	/**
	 * Data to be sent along with alert.
	 */
	@IgnoreField
	private Object data;
	
	/**
	 * Alert type.
	 */
	@IgnoreField
	private Object alertType;
	
	/**
	 * Used by pull alerts.
	 */
	private String target;
	
	/**
	 * Optional file that can be attached to alert.
	 */
	private FileInfo file;
	
	/**
	 * Flag indicating if this alert requires confirmation of alert
	 * recieval by target.
	 */
	private boolean requiresConfirmation;

	/**
	 * Gets the id of the alert.
	 *
	 * @return the id of the alert
	 */
	public long getId()
	{
		return id;
	}

	/**
	 * Sets the id of the alert.
	 *
	 * @param id the new id of the alert
	 */
	public void setId(long id)
	{
		this.id = id;
	}

	/**
	 * Gets the source which is generating this alert.
	 *
	 * @return the source which is generating this alert
	 */
	public String getSource()
	{
		return source;
	}

	/**
	 * Sets the source which is generating this alert.
	 *
	 * @param source the new source which is generating this alert
	 */
	public void setSource(String source)
	{
		this.source = source;
	}

	/**
	 * Gets the title of the alert.
	 *
	 * @return the title of the alert
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * Sets the title of the alert.
	 *
	 * @param title the new title of the alert
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}

	/**
	 * Gets the message to be sent.
	 *
	 * @return the message to be sent
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * Sets the message to be sent.
	 *
	 * @param message the new message to be sent
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}

	/**
	 * Gets the data to be sent along with alert.
	 *
	 * @return the data to be sent along with alert
	 */
	public Object getData()
	{
		return data;
	}

	/**
	 * Sets the data to be sent along with alert.
	 *
	 * @param data the new data to be sent along with alert
	 */
	public void setData(Object data)
	{
		this.data = data;
	}

	/**
	 * Gets the alert type.
	 *
	 * @return the alert type
	 */
	public Object getAlertType()
	{
		return alertType;
	}

	/**
	 * Sets the alert type.
	 *
	 * @param alertType the new alert type
	 */
	public void setAlertType(Object alertType)
	{
		this.alertType = alertType;
	}

	/**
	 * Gets the optional file that can be attached to alert.
	 *
	 * @return the optional file that can be attached to alert
	 */
	public FileInfo getFile()
	{
		return file;
	}

	/**
	 * Sets the optional file that can be attached to alert.
	 *
	 * @param file the new optional file that can be attached to alert
	 */
	public void setFile(FileInfo file)
	{
		this.file = file;
	}

	/**
	 * Gets the used by pull alerts.
	 *
	 * @return the used by pull alerts
	 */
	public String getTarget()
	{
		return target;
	}

	/**
	 * Sets the used by pull alerts.
	 *
	 * @param target the new used by pull alerts
	 */
	public void setTarget(String target)
	{
		this.target = target;
	}

	/**
	 * Checks if is flag indicating if this alert requires confirmation of alert recieval by target.
	 *
	 * @return the flag indicating if this alert requires confirmation of alert recieval by target
	 */
	public boolean isRequiresConfirmation()
	{
		return requiresConfirmation;
	}

	/**
	 * Sets the flag indicating if this alert requires confirmation of alert recieval by target.
	 *
	 * @param requiresConfirmation the new flag indicating if this alert requires confirmation of alert recieval by target
	 */
	public void setRequiresConfirmation(boolean requiresConfirmation)
	{
		this.requiresConfirmation = requiresConfirmation;
	}
}
