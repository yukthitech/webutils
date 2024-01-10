package com.yukthitech.webutils.common;

import java.util.Date;

import com.yukthitech.persistence.repository.annotations.Field;
import com.yukthitech.webutils.common.annotations.NonDisplayable;
import com.yukthitech.webutils.common.annotations.SearchFieldInfo;

/**
 * Base for search results including common search result fields.
 * @author akiran
 */
public class BasicSearchResult
{
	/**
	 * Id of the entity.
	 */
	@Field(value = "id")
	@NonDisplayable
	private long id;
	
	/**
	 * Name of the user who has created this entity.
	 */
	@Field(value = "createdBy.displayName")
	@SearchFieldInfo(order = 1000)
	private String createdBy;
	
	/**
	 * Created on time.
	 */
	@Field(value = "createdOn")
	@SearchFieldInfo(order = 1001)
	private Date createdOn;

	/**
	 * Name of the user who has updated this entity last time.
	 */
	@Field(value = "updatedBy.displayName")
	@SearchFieldInfo(order = 1002)
	private String updatedBy;
	
	/**
	 * Updated on.
	 */
	@Field(value = "updatedOn")
	@SearchFieldInfo(order = 1003)
	private Date updatedOn;

	/**
	 * Gets the id of the entity.
	 *
	 * @return the id of the entity
	 */
	public long getId()
	{
		return id;
	}

	/**
	 * Sets the id of the entity.
	 *
	 * @param id the new id of the entity
	 */
	public void setId(long id)
	{
		this.id = id;
	}

	/**
	 * Gets the name of the user who has created this entity.
	 *
	 * @return the name of the user who has created this entity
	 */
	public String getCreatedBy()
	{
		return createdBy;
	}

	/**
	 * Sets the name of the user who has created this entity.
	 *
	 * @param createdBy the new name of the user who has created this entity
	 */
	public void setCreatedBy(String createdBy)
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
	 * Gets the name of the user who has updated this entity last time.
	 *
	 * @return the name of the user who has updated this entity last time
	 */
	public String getUpdatedBy()
	{
		return updatedBy;
	}

	/**
	 * Sets the name of the user who has updated this entity last time.
	 *
	 * @param updatedBy the new name of the user who has updated this entity last time
	 */
	public void setUpdatedBy(String updatedBy)
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
