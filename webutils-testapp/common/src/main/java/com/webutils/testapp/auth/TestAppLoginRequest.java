package com.webutils.testapp.auth;

import com.yukthitech.validation.annotations.NotEmpty;

import lombok.Data;

/**
 * Login request for the testapp login page. Accepts short passwords (e.g. {@code test})
 * unlike framework {@code LoginRequest} which requires {@code @MinLen(8)}.
 */
@Data
public class TestAppLoginRequest
{
	@NotEmpty
	private String username;

	@NotEmpty
	private String password;

	private String userSpace;
}
