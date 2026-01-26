package com.webutils.common.auth;

import com.yukthitech.validation.annotations.MaxLen;
import com.yukthitech.validation.annotations.MinLen;
import com.yukthitech.validation.annotations.NotEmpty;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class LoginRequest
{
	@NotEmpty
	@Email
	private String mailId;

	@NotEmpty
	@MinLen(8)
	@MaxLen(100)
	private String password;
	
	private String userSpace;
}
