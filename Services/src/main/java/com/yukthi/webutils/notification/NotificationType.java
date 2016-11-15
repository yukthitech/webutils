package com.yukthi.webutils.notification;

/**
 * Type of the notification. Optional notifications can be opted by user for notification.
 * @author akiran
 */
public class NotificationType
{
	/**
	 * Name of the notification.
	 */
	private String name;
	
	/**
	 * Description about the notification.
	 */
	private String description;
	
	/**
	 * Flag indicating if the notification is optional or mandatory.
	 */
	private boolean optional;
	
	/**
	 * Indicates whether notification is enabled by default or not.
	 */
	private boolean defaultEnabled;
	
	/**
	 * Instantiates a new notification type.
	 */
	public NotificationType()
	{}

	/**
	 * Instantiates a new notification type.
	 *
	 * @param name the name
	 * @param description the description
	 * @param optional the optional
	 * @param defaultEnabled the default enabled
	 */
	public NotificationType(String name, String description, boolean optional, boolean defaultEnabled)
	{
		this.name = name;
		this.description = description;
		this.optional = optional;
		this.defaultEnabled = defaultEnabled;
	}

	/**
	 * Gets the name of the notification.
	 *
	 * @return the name of the notification
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the notification.
	 *
	 * @param name the new name of the notification
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the description about the notification.
	 *
	 * @return the description about the notification
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Sets the description about the notification.
	 *
	 * @param description the new description about the notification
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * Checks if is flag indicating if the notification is optional or mandatory.
	 *
	 * @return the flag indicating if the notification is optional or mandatory
	 */
	public boolean isOptional()
	{
		return optional;
	}

	/**
	 * Sets the flag indicating if the notification is optional or mandatory.
	 *
	 * @param optional the new flag indicating if the notification is optional or mandatory
	 */
	public void setOptional(boolean optional)
	{
		this.optional = optional;
	}

	/**
	 * Checks if is indicates whether notification is enabled by default or not.
	 *
	 * @return the indicates whether notification is enabled by default or not
	 */
	public boolean isDefaultEnabled()
	{
		return defaultEnabled;
	}

	/**
	 * Sets the indicates whether notification is enabled by default or not.
	 *
	 * @param defaultEnabled the new indicates whether notification is enabled by default or not
	 */
	public void setDefaultEnabled(boolean defaultEnabled)
	{
		this.defaultEnabled = defaultEnabled;
	}
}
