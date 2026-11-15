package com.webutils.common;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface IWebUtilsConstants
{
	ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
    String SESSION_TOKEN_HEADER = "Authorization";
    String SESSION_BEARER_PREFIX = "Bearer ";

	/**
	 * Role required by test-only APIs such as runtime config mutation.
	 * Granted only to users whose {@code CUSTOM_SPACE} is {@link #TEST_USER_SPACE}.
	 */
	String ROLE_TEST = "TEST";

	/**
	 * User space for the seeded test user. Do not use for product roles.
	 */
	String TEST_USER_SPACE = "test";
}
