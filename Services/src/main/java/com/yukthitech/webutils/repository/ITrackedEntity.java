package com.yukthitech.webutils.repository;

import java.util.Date;

/**
 * Used to mark entity as tenant space based.
 * @author akiran
 */
public interface ITrackedEntity
{
	/**
	 * Gets the created by user.
	 *
	 * @return the created by user
	 */
	public UserEntity getCreatedBy();
	
	/**
	 * Sets the created by user.
	 *
	 * @param createdBy the new created by user
	 */
	public void setCreatedBy(UserEntity createdBy);

	/**
	 * Gets the created on time.
	 *
	 * @return the created on time
	 */
	public Date getCreatedOn();

	/**
	 * Sets the created on time.
	 *
	 * @param createdOn the new created on time
	 */
	public void setCreatedOn(Date createdOn);

	/**
	 * Gets the updating user.
	 *
	 * @return the updating user
	 */
	public UserEntity getUpdatedBy();

	/**
	 * Sets the updating user.
	 *
	 * @param updatedBy the new updating user
	 */
	public void setUpdatedBy(UserEntity updatedBy);

	/**
	 * Gets the updated on.
	 *
	 * @return the updated on
	 */
	public Date getUpdatedOn();
	
	/**
	 * Sets the updated on.
	 *
	 * @param updatedOn the new updated on
	 */
	public void setUpdatedOn(Date updatedOn);
}
