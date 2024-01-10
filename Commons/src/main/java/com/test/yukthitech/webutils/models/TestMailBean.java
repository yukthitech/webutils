package com.test.yukthitech.webutils.models;

import com.yukthitech.validation.annotations.NotEmpty;
import com.yukthitech.webutils.common.annotations.Model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * Test bean used by client test cases to ensure spring validation is enabled
 */
@Model(name = "TestMailBean")
public class TestMailBean
{
	@NotEmpty
	private String name;

	@Min(18)
	@Max(30)
	private Integer age;

	private String toMailId;

	/**
	 * Instantiates a new test bean.
	 */
	public TestMailBean()
	{}

	public TestMailBean(String name, Integer age, String toMailId)
	{
		this.name = name;
		this.age = age;
		this.toMailId = toMailId;
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
	 * @param name
	 *            the new name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	public Integer getAge()
	{
		return age;
	}

	public void setAge(Integer age)
	{
		this.age = age;
	}

	public String getToMailId()
	{
		return toMailId;
	}

	public void setToMailId(String toMailId)
	{
		this.toMailId = toMailId;
	}
}
