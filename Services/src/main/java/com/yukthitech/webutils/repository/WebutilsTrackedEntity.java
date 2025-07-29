package com.yukthitech.webutils.repository;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ManyToOne;

import com.yukthitech.persistence.annotations.DataType;
import com.yukthitech.persistence.annotations.DataTypeMapping;
import com.yukthitech.persistence.annotations.NotUpdateable;
import com.yukthitech.webutils.user.UserEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Base class for entities containing common fields for tracking.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
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
	 *
	 * @param id the id
	 */
	public WebutilsTrackedEntity(Long id)
	{
		super.setId(id);
	}
}
