package com.yukthitech.webutils.common;

import com.yukthitech.webutils.common.annotations.Model;

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
	private Long id;

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
}
