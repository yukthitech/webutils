package com.webutils.common.form.captcha;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Used to mark a field as Captcha field.
 * @author akiran
 */
@Constraint(validatedBy = CaptchaValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Captcha
{
	public String message() default "Invalid captcha value specified";

	/**
	 * Message used when the captcha token exists but has expired.
	 * Users should refresh the captcha (or reload the page) and try again.
	 */
	public String expiredMessage() default "Captcha has expired. Please refresh the captcha or reload the page and try again.";

	public Class<?>[] groups() default {};

	public Class<? extends Payload>[] payload() default {};
}
