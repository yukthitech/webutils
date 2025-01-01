package com.yukthitech.webutils.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.yukthitech.webutils.common.verification.VerificationType;

/**
 * Used to mark a String field which needs verification. 
 * 
 * @author Kranthi
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NeedVerification 
{
	/**
	 * Type of verification required by the field.
	 * @return
	 */
	public VerificationType type();
}
