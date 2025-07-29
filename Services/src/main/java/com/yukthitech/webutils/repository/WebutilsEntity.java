package com.yukthitech.webutils.repository;

import javax.persistence.Column;

import com.yukthitech.persistence.annotations.NotUpdateable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Base class for entities containing common fields for tracking and space separation.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
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
	 *
	 * @param id the id
	 */
	public WebutilsEntity(Long id)
	{
		super.setId(id);
	}
}
