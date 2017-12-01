package com.yukthitech.webutils.notification;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.yukthitech.persistence.annotations.UniqueConstraint;
import com.yukthitech.persistence.annotations.UniqueConstraints;
import com.yukthitech.webutils.repository.UserEntity;
import com.yukthitech.webutils.repository.WebutilsEntity;

/**
 * Used to set user notification preferences.
 * @author akiran
 */
@Table(name = "WEBUTILS_USER_NOTIFICATION")
@UniqueConstraints({
	@UniqueConstraint(name = "USER_NOTIFICATION", fields = {"user", "notificationType"})
	})
public class UserNotificationEntity extends WebutilsEntity
{
	/**
	 * User for which notification preference is being persisted.
	 */
	@ManyToOne
	@Column(name = "USER_ID", nullable = false)
	private UserEntity user;
	
	/**
	 * Notification type.
	 */
	@Column(name = "NOTIFICATION_TYPE", nullable = false)
	private String notificationType;
	
	/**
	 * Indicates whether this notification is enabled or not for specified user.
	 */
	@Column(name = "ENABLED", nullable = false)
	private boolean enabled;
	
	/**
	 * Instantiates a new user notification entity.
	 */
	public UserNotificationEntity()
	{}

	/**
	 * Instantiates a new user notification entity.
	 *
	 * @param user the user
	 * @param notificationType the notification type
	 * @param enabled the enabled
	 */
	public UserNotificationEntity(UserEntity user, String notificationType, boolean enabled)
	{
		this.user = user;
		this.notificationType = notificationType;
		this.enabled = enabled;
	}

	/**
	 * Gets the user for which notification preference is being persisted.
	 *
	 * @return the user for which notification preference is being persisted
	 */
	public UserEntity getUser()
	{
		return user;
	}

	/**
	 * Sets the user for which notification preference is being persisted.
	 *
	 * @param user the new user for which notification preference is being persisted
	 */
	public void setUser(UserEntity user)
	{
		this.user = user;
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
	 * Checks if is indicates whether this notification is enabled or not for specified user.
	 *
	 * @return the indicates whether this notification is enabled or not for specified user
	 */
	public boolean isEnabled()
	{
		return enabled;
	}

	/**
	 * Sets the indicates whether this notification is enabled or not for specified user.
	 *
	 * @param enabled the new indicates whether this notification is enabled or not for specified user
	 */
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
}
