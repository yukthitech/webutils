package com.yukthitech.webutils.appconfig;

import javax.persistence.Column;
import javax.persistence.Table;

import com.yukthitech.persistence.annotations.DataType;
import com.yukthitech.persistence.annotations.DataTypeMapping;
import com.yukthitech.persistence.conversion.impl.JsonWithTypeConverter;
import com.yukthitech.webutils.repository.WebutilsEntity;

/**
 * Represents app config, a simple name-value pairs for storing dynamic config.
 */
@Table(name = "WEBUTILS_APP_CONFIG")
public class AppConfigEntity extends WebutilsEntity
{
	/**
	 * Name of the rule.
	 */
	@Column(name = "NAME", length = 100, nullable = false, unique = true)
	private String name;
	
	/**
	 * Value of the config entry.
	 */
	@Column(name = "CONFIG_VALUE", length = 1000)
	@DataTypeMapping(converterType = JsonWithTypeConverter.class, type = DataType.STRING)
	private Object value;
	
	/**
	 * Instantiates a new app config entity.
	 */
	public AppConfigEntity()
	{}

	/**
	 * Instantiates a new app config entity.
	 *
	 * @param name the name
	 * @param value the value
	 */
	public AppConfigEntity(String name, Object value)
	{
		this.name = name;
		this.value = value;
	}

	/**
	 * Gets the name of the rule.
	 *
	 * @return the name of the rule
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the rule.
	 *
	 * @param name the new name of the rule
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the value of the config entry.
	 *
	 * @return the value of the config entry
	 */
	public Object getValue()
	{
		return value;
	}

	/**
	 * Sets the value of the config entry.
	 *
	 * @param value the new value of the config entry
	 */
	public void setValue(Object value)
	{
		this.value = value;
	}
}
