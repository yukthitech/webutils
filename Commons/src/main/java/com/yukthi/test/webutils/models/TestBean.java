package com.yukthi.test.webutils.models;

import javax.validation.constraints.NotNull;

/**
 * Test bean used by client test cases to ensure spring validation is enabled
 */
public class TestBean
{
	/** The name. */
	@NotNull
	private String name;

	/**
	 * Instantiates a new test bean.
	 */
	public TestBean()
	{}
	
	/**
	 * Instantiates a new test bean.
	 *
	 * @param name the name
	 */
	public TestBean(String name)
	{
		this.name = name;
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
