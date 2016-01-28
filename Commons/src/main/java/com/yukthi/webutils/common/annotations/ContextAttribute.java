package com.yukthi.webutils.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * For dynamic methods used to fetch attribute from {@link WebutilsContext} object.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface ContextAttribute
{
	/**
	 * Name of the context attribute
	 * @return Name of the context attribute
	 */
	public String value();
}
