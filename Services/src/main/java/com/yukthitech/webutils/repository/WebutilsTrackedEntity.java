package com.yukthitech.webutils.repository;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ManyToOne;

import com.yukthitech.persistence.annotations.DataType;
import com.yukthitech.persistence.annotations.DataTypeMapping;
import com.yukthitech.persistence.annotations.NotUpdateable;

/**
 * Base class for entities containing common fields for tracking.
 */
public abstract class WebutilsTrackedEntity extends WebutilsBaseEntity implements ITrackedEntity
{
	/**
	 * Created by user.
	 */
	@NotUpdateable
	@ManyToOne
	@Column(name = "CREATED_BY_ID")
	private UserEntity createdBy;
	
	/**
	 * Created on time.
	 */
	@NotUpdateable
	@Column(name = "CREATED_ON", nullable = false)
	@DataTypeMapping(type = DataType.DATE_TIME)
	private Date createdOn = new Date();

	/**
	 * Updating user.
	 */
	@ManyToOne
	@Column(name = "UPDATED_BY_ID")
	private UserEntity updatedBy;
	
	/**
	 * Updated on.
	 */
	@Column(name = "UPDATED_ON", nullable = false)
	@DataTypeMapping(type = DataType.DATE_TIME)
	private Date updatedOn = new Date();

	/**
	 * Instantiates a new base entity.
	 */
	public WebutilsTrackedEntity()
	{}
	
	/**
	 * Instantiates a new base entity.
	 *
	 * @param id the id
	 */
	public WebutilsTrackedEntity(Long id)
	{
		super.setId(id);
	}

	/**
	 * Gets the created by user.
	 *
	 * @return the created by user
	 */
	public UserEntity getCreatedBy()
	{
		return createdBy;
	}

	/**
	 * Sets the created by user.
	 *
	 * @param createdBy the new created by user
	 */
	public void setCreatedBy(UserEntity createdBy)
	{
		this.createdBy = createdBy;
	}

	/**
	 * Gets the created on time.
	 *
	 * @return the created on time
	 */
	public Date getCreatedOn()
	{
		return createdOn;
	}

	/**
	 * Sets the created on time.
	 *
	 * @param createdOn the new created on time
	 */
	public void setCreatedOn(Date createdOn)
	{
		this.createdOn = createdOn;
	}

	/**
	 * Gets the updating user.
	 *
	 * @return the updating user
	 */
	public UserEntity getUpdatedBy()
	{
		return updatedBy;
	}

	/**
	 * Sets the updating user.
	 *
	 * @param updatedBy the new updating user
	 */
	public void setUpdatedBy(UserEntity updatedBy)
	{
		this.updatedBy = updatedBy;
	}

	/**
	 * Gets the updated on.
	 *
	 * @return the updated on
	 */
	public Date getUpdatedOn()
	{
		return updatedOn;
	}

	/**
	 * Sets the updated on.
	 *
	 * @param updatedOn the new updated on
	 */
	public void setUpdatedOn(Date updatedOn)
	{
		this.updatedOn = updatedOn;
	}
}
