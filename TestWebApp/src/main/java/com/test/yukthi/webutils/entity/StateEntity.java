package com.test.yukthi.webutils.entity;

import javax.persistence.Table;

import com.yukthitech.persistence.annotations.UniqueConstraint;
import com.yukthitech.persistence.annotations.UniqueConstraints;
import com.yukthitech.webutils.repository.WebutilsEntity;

/**
 * The Class StateEntity.
 */
@Table(name = "STATE")
@UniqueConstraints({
	@UniqueConstraint(fields = {"name"}, name = "UQ_CITY_NAME")
})
public class StateEntity extends WebutilsEntity
{
	/**
	 * name.
	 */
	private String name;
	
	public StateEntity()
	{
	}
	
	public StateEntity(String name, String spaceIdentity)
	{
		this.name = name;
		setSpaceIdentity(spaceIdentity);
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
