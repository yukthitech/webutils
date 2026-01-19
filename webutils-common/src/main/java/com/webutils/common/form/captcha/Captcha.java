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

	public Class<?>[] groups() default {};

	public Class<? extends Payload>[] payload() default {};
}
