package com.webutils.common.form.otp;

/**
 * Purpose for which an OTP was issued. Persisted in {@code FORM_TOKEN} so a token
 * can only be verified for the same purpose (e.g. login OTP cannot reset password).
 */
public enum OtpPurpose
{
	/**
	 * Pre-auth OTP for login ({@code /api/auth/otp/send-login} → {@code /api/auth/login-otp}).
	 */
	LOGIN,

	/**
	 * Pre-auth OTP for password reset ({@code /api/auth/forgot-password} → {@code /api/auth/reset-password}).
	 */
	RESET,

	/**
	 * Authenticated contact / form field verification ({@code /api/otp/send/...}).
	 */
	VERIFICATION
}
