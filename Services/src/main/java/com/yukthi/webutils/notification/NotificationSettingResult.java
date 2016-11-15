package com.yukthi.webutils.notification;

import com.yukthi.persistence.repository.annotations.Field;

/**
 * Search result in querying for a notification type.
 * @author akiran
 */
public class NotificationSettingResult
{
	/**
	 * User id.
	 */
	@Field("user.id")
	private Long userId;
	
	/**
	 * If queried notification is enabled or not.
	 */
	@Field("enabled")
	private boolean enabled;

	/**
	 * Gets the user id.
	 *
	 * @return the user id
	 */
	public Long getUserId()
	{
		return userId;
	}

	/**
	 * Sets the user id.
	 *
	 * @param userId the new user id
	 */
	public void setUserId(Long userId)
	{
		this.userId = userId;
	}

	/**
	 * Checks if is if queried notification is enabled or not.
	 *
	 * @return the if queried notification is enabled or not
	 */
	public boolean isEnabled()
	{
		return enabled;
	}

	/**
	 * Sets the if queried notification is enabled or not.
	 *
	 * @param enabled the new if queried notification is enabled or not
	 */
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
}
