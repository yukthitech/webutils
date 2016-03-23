package com.test.yukthi.webutils.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.yukthi.persistence.annotations.UniqueConstraint;
import com.yukthi.persistence.annotations.UniqueConstraints;

/**
 * The Class StateEntity.
 */
@Table(name = "STATE")
@UniqueConstraints({
	@UniqueConstraint(fields = {"name"}, name = "UQ_CITY_NAME")
})
public class StateEntity
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private long id;

	/**
	 * name.
	 */
	private String name;
	
	public StateEntity()
	{
	}
	
	public StateEntity(String name)
	{
		this.name = name;
	}



	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
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
