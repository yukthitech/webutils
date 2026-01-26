package com.webutils.common.form.otp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Used to mark a String field which needs verification. 
 * 
 * @author Kranthi
 */
@Constraint(validatedBy = OtpValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Otp 
{
	public String message() default "Invalid captcha value specified";

	public Class<?>[] groups() default {};

	public Class<? extends Payload>[] payload() default {};

	/**
	 * Type of verification required by the field.
	 * @return
	 */
	public VerificationType type();
}
