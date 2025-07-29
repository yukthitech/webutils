package com.yukthitech.webutils.repository;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

import lombok.Data;

/**
 * Base class for entities containing common fields like id and version.
 */
@Data
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
}
