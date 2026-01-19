package com.webutils.common.form.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * For dynamic methods used to fetch attribute from WebutilsContext object.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface ContextAttribute
{
	/**
	 * Property expression that can be executed on WebutilsContext.attributeMap.
	 * Eg: currentUser.userId
	 * @return Name of the context attribute
	 */
	public String value();
}
