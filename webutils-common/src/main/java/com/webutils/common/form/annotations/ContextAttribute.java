package com.webutils.common.form.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * For search query fields used to fetch a value from the search execution context.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface ContextAttribute
{
	/**
	 * FreeMarker expression executed on the search execution context.
	 * Eg: currentUser.id
	 */
	public String value();
}
