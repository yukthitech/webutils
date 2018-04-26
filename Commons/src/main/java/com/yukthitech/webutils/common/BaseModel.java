package com.yukthitech.webutils.common;

import com.yukthitech.persistence.repository.annotations.Field;
import com.yukthitech.webutils.common.annotations.Model;
import com.yukthitech.webutils.common.annotations.NonDisplayable;

/**
 * Base class for model classes.
 * @author akiran
 */
@Model
public class BaseModel
{
	/**
	 * Id for the model.
	 */
	@Field("id")
	@NonDisplayable
	private Long id;
	
	/**
	 * Version of entity, used for optimistic locking.
	 */
	@NonDisplayable
	private Integer version;

	/**
	 * Gets the id for the model.
	 *
	 * @return the id for the model
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * Sets the id for the model.
	 *
	 * @param id the new id for the model
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * Gets the version of entity, used for optimistic locking.
	 *
	 * @return the version of entity, used for optimistic locking
	 */
	public Integer getVersion()
	{
		return version;
	}

	/**
	 * Sets the version of entity, used for optimistic locking.
	 *
	 * @param version the new version of entity, used for optimistic locking
	 */
	public void setVersion(Integer version)
	{
		this.version = version;
	}
}
