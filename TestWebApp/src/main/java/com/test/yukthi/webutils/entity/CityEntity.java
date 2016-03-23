package com.test.yukthi.webutils.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.yukthi.persistence.annotations.UniqueConstraint;
import com.yukthi.persistence.annotations.UniqueConstraints;

/**
 * The Class CityEntity.
 */
@Table(name = "CITY")
public class CityEntity
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private long id;

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

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
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
