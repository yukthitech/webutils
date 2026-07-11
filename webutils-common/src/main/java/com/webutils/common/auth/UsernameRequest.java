package com.webutils.common.auth;

import com.yukthitech.validation.annotations.MaxLen;
import com.yukthitech.validation.annotations.NotEmpty;

import lombok.Data;

/**
 * Request carrying username (email or mobile) and optional user space. {@code username} is the username: email or mobile.
 */
@Data
public class UsernameRequest
{
	/**
	 * Username — email (contains {@code @}) or mobile number.
	 */
	@NotEmpty
	@MaxLen(255)
	private String username;

	private String userSpace;
}
