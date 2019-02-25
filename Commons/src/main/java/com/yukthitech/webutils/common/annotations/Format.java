package com.yukthitech.webutils.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Can be used to define format to be used for string conversion for user presentation (used on field like dates)
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Format
{
	/**
	 * Format to be used.
	 * @return label value
	 */
	public String value();
}
