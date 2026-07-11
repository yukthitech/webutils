package com.webutils.common.auth;

import com.webutils.common.form.otp.OtpVerification;
import com.yukthitech.validation.annotations.MaxLen;
import com.yukthitech.validation.annotations.NotEmpty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * OTP-based login request. {@code username} is the username: email or mobile.
 */
@Data
public class LoginOtpRequest
{
	@NotEmpty
	@MaxLen(255)
	private String username;

	@NotNull
	@Valid
	private OtpVerification otp;

	private String userSpace;
}
