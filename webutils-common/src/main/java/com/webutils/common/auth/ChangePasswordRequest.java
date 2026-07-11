package com.webutils.common.auth;

import com.yukthitech.validation.annotations.MaxLen;
import com.yukthitech.validation.annotations.MinLen;
import com.yukthitech.validation.annotations.NotEmpty;

import lombok.Data;

/**
 * Change password while authenticated.
 */
@Data
public class ChangePasswordRequest
{
	@NotEmpty
	@MinLen(8)
	@MaxLen(100)
	private String currentPassword;

	@NotEmpty
	@MinLen(8)
	@MaxLen(100)
	private String newPassword;
}
