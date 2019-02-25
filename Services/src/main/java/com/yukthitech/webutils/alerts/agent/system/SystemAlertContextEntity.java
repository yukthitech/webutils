package com.yukthitech.webutils.alerts.agent.system;

import javax.persistence.Column;
import javax.persistence.Table;

import com.yukthitech.persistence.annotations.DataType;
import com.yukthitech.persistence.annotations.DataTypeMapping;
import com.yukthitech.persistence.annotations.UniqueConstraint;
import com.yukthitech.persistence.annotations.UniqueConstraints;
import com.yukthitech.persistence.conversion.impl.JsonWithTypeConverter;
import com.yukthitech.webutils.repository.WebutilsEntity;

/**
 * Represents the app alerts context.
 */
@Table(name = "WEBUTILS_APP_ALERT_CONTEXT")
@UniqueConstraints(
	@UniqueConstraint(name = "ATTR_NAME", fields = {"name"}, finalName = false)
)
public class SystemAlertContextEntity extends WebutilsEntity
{
	/**
	 * Name of context attribute.
	 */
	@Column(name = "NAME", length = 100, nullable = false)
	private String name;
	
	/**
	 * Value of the attribute.
	 */
	@Column(name = "ATTR_VALUE", length = 1000)
	@DataTypeMapping(converterType = JsonWithTypeConverter.class, type = DataType.STRING)
	private Object value;

	/**
	 * Gets the name of context attribute.
	 *
	 * @return the name of context attribute
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of context attribute.
	 *
	 * @param name the new name of context attribute
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the value of the attribute.
	 *
	 * @return the value of the attribute
	 */
	public Object getValue()
	{
		return value;
	}

	/**
	 * Sets the value of the attribute.
	 *
	 * @param value the new value of the attribute
	 */
	public void setValue(Object value)
	{
		this.value = value;
	}
}
