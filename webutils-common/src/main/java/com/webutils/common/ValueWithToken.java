package com.webutils.common;

import com.yukthitech.validation.IStringConvertible;
import com.yukthitech.validation.annotations.NotEmpty;

import lombok.Data;

/**
 * Encapsulation of value and validation token.
 */
@Data
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

	@Override
	public String toStringValue()
	{
		return value;
	}
}
