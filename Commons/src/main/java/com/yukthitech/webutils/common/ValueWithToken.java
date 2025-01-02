package com.yukthitech.webutils.common;

import com.yukthitech.validation.IStringConvertible;
import com.yukthitech.validation.annotations.NotEmpty;
import com.yukthitech.webutils.common.annotations.Model;

/**
 * Encapsulation of value and validation token.
 */
@Model
public class ValueWithToken implements IStringConvertible
{
	/**
	 * Actual value for the field.
	 */
	@NotEmpty
	private String value;
	
	/**
	 * Verification token.
	 */
	@NotEmpty
	private String token;

	/**
	 * Gets the actual value for the field.
	 *
	 * @return the actual value for the field
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * Sets the actual value for the field.
	 *
	 * @param value the new actual value for the field
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

	/**
	 * Gets the verification token.
	 *
	 * @return the verification token
	 */
	public String getToken()
	{
		return token;
	}

	/**
	 * Sets the verification token.
	 *
	 * @param token the new verification token
	 */
	public void setToken(String token)
	{
		this.token = token;
	}
	
	@Override
	public String toStringValue()
	{
		return value;
	}
}
