package com.webutils.common.auth;

import com.webutils.common.form.otp.OtpVerification;
import com.yukthitech.validation.annotations.MaxLen;
import com.yukthitech.validation.annotations.MinLen;
import com.yukthitech.validation.annotations.NotEmpty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Complete password reset with OTP and new password. {@code username} is email or mobile.
 */
@Data
public class ResetPasswordRequest
{
	@NotEmpty
	@MaxLen(255)
	private String username;

	@NotNull
	@Valid
	private OtpVerification otp;

	@NotEmpty
	@MinLen(8)
	@MaxLen(100)
	private String newPassword;

	private String userSpace;
}
