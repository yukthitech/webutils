package com.yukthitech.webutils.repository;

import javax.persistence.Column;

import com.yukthitech.persistence.annotations.NotUpdateable;

/**
 * Base class for entities containing common fields for tracking and space separation.
 */
public abstract class WebutilsEntity extends WebutilsTrackedEntity implements ITenantSpaceBased
{
	/**
	 * Space identity.
	 */
	@NotUpdateable
	@Column(name = "SPACE_IDENTITY", length = 150, nullable = false)
	private String spaceIdentity = "";

	/**
	 * Instantiates a new base entity.
	 */
	public WebutilsEntity()
	{}
	
	/**
	 * Instantiates a new base entity.
	 *
	 * @param id the id
	 */
	public WebutilsEntity(Long id)
	{
		super.setId(id);
	}

	/**
	 * Gets the space identity.
	 *
	 * @return the space identity
	 */
	public String getSpaceIdentity()
	{
		return spaceIdentity;
	}

	/**
	 * Sets the space identity.
	 *
	 * @param spaceIdentity the new space identity
	 */
	public void setSpaceIdentity(String spaceIdentity)
	{
		this.spaceIdentity = spaceIdentity;
	}
}
