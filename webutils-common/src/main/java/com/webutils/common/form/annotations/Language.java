package com.webutils.common.form.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Marks a string field as language content (JSON / XML / JSON Schema / Python) edited via CodeMirror.
 * Content is validated on the server according to {@link #value()} (Python currently has no syntax check).
 *
 * @author akiran
 */
@Constraint(validatedBy = LanguageValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Language
{
	/**
	 * Language / content type of the field.
	 */
	public LanguageType value();

	public String message() default "Invalid language content";

	public Class<?>[] groups() default {};

	public Class<? extends Payload>[] payload() default {};
}
