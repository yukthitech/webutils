package com.yukthitech.webutils.common.models.notification;

/**
 * Model representing the notification setting for user.
 * @author akiran
 */
public class NotificationSetting
{
	/**
	 * Notification type.
	 */
	private String notificationType;
	
	/**
	 * Flag indicating if this notification is enabled or not.
	 */
	private boolean enabled;
	
	/**
	 * Instantiates a new notification model.
	 */
	public NotificationSetting()
	{}

	/**
	 * Gets the notification type.
	 *
	 * @return the notification type
	 */
	public String getNotificationType()
	{
		return notificationType;
	}

	/**
	 * Sets the notification type.
	 *
	 * @param notificationType the new notification type
	 */
	public void setNotificationType(String notificationType)
	{
		this.notificationType = notificationType;
	}

	/**
	 * Checks if is flag indicating if this notification is enabled or not.
	 *
	 * @return the flag indicating if this notification is enabled or not
	 */
	public boolean isEnabled()
	{
		return enabled;
	}

	/**
	 * Sets the flag indicating if this notification is enabled or not.
	 *
	 * @param enabled the new flag indicating if this notification is enabled or not
	 */
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
}
