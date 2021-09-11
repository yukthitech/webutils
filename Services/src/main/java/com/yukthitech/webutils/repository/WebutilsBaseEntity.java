package com.yukthitech.webutils.repository;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

/**
 * Base class for entities containing common fields like id and version.
 */
public abstract class WebutilsBaseEntity
{
	/**
	 * Primary key of the entity.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	/**
	 * Version of the entity.
	 */
	@Column(name = "VERSION")
	@Version
	private Integer version = 1;

	/**
	 * Instantiates a new base entity.
	 */
	public WebutilsBaseEntity()
	{}
	
	/**
	 * Instantiates a new base entity.
	 *
	 * @param id the id
	 */
	public WebutilsBaseEntity(Long id)
	{
		this.id = id;
	}

	/**
	 * Gets the primary key of the entity.
	 *
	 * @return the primary key of the entity
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * Sets the primary key of the entity.
	 *
	 * @param id the new primary key of the entity
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * Gets the version of the entity.
	 *
	 * @return the version of the entity
	 */
	public Integer getVersion()
	{
		return version;
	}

	/**
	 * Sets the version of the entity.
	 *
	 * @param version the new version of the entity
	 */
	public void setVersion(Integer version)
	{
		this.version = version;
	}

}
