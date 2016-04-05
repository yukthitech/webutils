package com.yukthi.webutils.repository;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import com.yukthi.persistence.annotations.DataType;
import com.yukthi.persistence.annotations.DataTypeMapping;
import com.yukthi.persistence.annotations.Index;
import com.yukthi.persistence.annotations.Indexes;
import com.yukthi.persistence.annotations.NotUpdateable;

/**
 * Base class for entities containing common fields for tracking and space separation.
 */
@Indexes({
	@Index(name = "SPACE_IDENTITY_IDX", fields = {"spaceIdentity"})
	})
public abstract class WebutilsEntity
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
	 * Space identity.
	 */
	@NotUpdateable
	@Column(name = "SPACE_IDENTITY", length = 150, nullable = false)
	private String spaceIdentity = "";
	
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
	@Column(name = "CREATED_ON")
	@DataTypeMapping(type = DataType.DATE_TIME)
	private Date createdOn;

	/**
	 * Updating user.
	 */
	@ManyToOne
	@Column(name = "UPDATED_BY_ID")
	private UserEntity updatedBy;
	
	/**
	 * Updated on.
	 */
	@Column(name = "UPDATED_ON")
	@DataTypeMapping(type = DataType.DATE_TIME)
	private Date updatedOn;

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
