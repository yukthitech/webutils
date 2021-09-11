package com.test.yukthitech.webutils.entity;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.yukthitech.webutils.repository.WebutilsBaseEntity;

/**
 * The Class CityEntity.
 */
@Table(name = "CITY")
public class CityEntity extends WebutilsBaseEntity
{
	/**
	 * Name.
	 */
	private String name;
	
	@ManyToOne
	@Column(name = "STATE_ID", nullable = false)
	private StateEntity state;

	public CityEntity()
	{}
	
	public CityEntity(String name, StateEntity state)
	{
		this.name = name;
		this.state = state;
	}

	public StateEntity getState()
	{
		return state;
	}

	public void setState(StateEntity state)
	{
		this.state = state;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name)
	{
		this.name = name;
	}
}
