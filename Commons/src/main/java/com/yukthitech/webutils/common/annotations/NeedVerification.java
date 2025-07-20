package com.yukthitech.webutils.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.yukthitech.webutils.common.verification.VerificationType;

import jakarta.validation.Payload;

/**
 * Used to mark a String field which needs verification. 
 * 
 * @author Kranthi
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NeedVerification 
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
