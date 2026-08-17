package com.webutils.common.form.captcha;

/**
 * Result of captcha token + value validation.
 */
public enum CaptchaValidationResult
{
	/**
	 * Token exists, is unexpired, and the submitted value matches.
	 */
	VALID,

	/**
	 * Token exists in storage but its expiry time has passed.
	 */
	EXPIRED,

	/**
	 * Token is unexpired but the submitted value does not match.
	 */
	INVALID_VALUE,

	/**
	 * No token row exists (never issued or already removed).
	 */
	NOT_FOUND
}
