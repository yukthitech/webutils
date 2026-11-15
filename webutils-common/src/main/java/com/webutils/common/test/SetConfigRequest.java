package com.webutils.common.test;

import com.yukthitech.validation.annotations.NotEmpty;

import lombok.Data;

/**
 * Test-only request to mutate an in-memory Spring bean field via reflection.
 */
@Data
public class SetConfigRequest
{
	@NotEmpty
	private String className;

	@NotEmpty
	private String propertyName;

	@NotEmpty
	private String value;
}
