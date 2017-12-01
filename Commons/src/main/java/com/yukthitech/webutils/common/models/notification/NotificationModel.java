package com.yukthitech.webutils.common.models.notification;

import com.yukthitech.persistence.repository.annotations.Field;

/**
 * Model representing the notification setting for user.
 * @author akiran
 */
public class NotificationModel
{
	/**
	 * Notification type.
	 */
	@Field("notificationType")
	private String notificationType;
	
	/**
	 * Flag indicating if this notification is enabled or not.
	 */
	@Field("enabled")
	private boolean enabled;
	
	/**
	 * Field indicating if this field is optional or not.
	 */
	private boolean optional;
	
	/**
	 * Description about this notification.
	 */
	private String descritpion; 
	
	/**
	 * Instantiates a new notification model.
	 */
	public NotificationModel()
	{}

	/**
	 * Instantiates a new notification model.
	 *
	 * @param notificationType the notification type
	 * @param enabled the enabled
	 * @param optional the optional
	 * @param descritpion the descritpion
	 */
	public NotificationModel(String notificationType, boolean enabled, boolean optional, String descritpion)
	{
		this.notificationType = notificationType;
		this.enabled = enabled;
		this.optional = optional;
		this.descritpion = descritpion;
	}

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

	/**
	 * Checks if is field indicating if this field is optional or not.
	 *
	 * @return the field indicating if this field is optional or not
	 */
	public boolean isOptional()
	{
		return optional;
	}

	/**
	 * Sets the field indicating if this field is optional or not.
	 *
	 * @param optional the new field indicating if this field is optional or not
	 */
	public void setOptional(boolean optional)
	{
		this.optional = optional;
	}

	/**
	 * Gets the description about this notification.
	 *
	 * @return the description about this notification
	 */
	public String getDescritpion()
	{
		return descritpion;
	}

	/**
	 * Sets the description about this notification.
	 *
	 * @param descritpion the new description about this notification
	 */
	public void setDescritpion(String descritpion)
	{
		this.descritpion = descritpion;
	}
}
