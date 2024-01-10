package com.yukthitech.webutils.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.servlet.http.HttpServletRequest;

/**
 * For dynamic methods used to fetch attribute from {@link HttpServletRequest}.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface RequestParam
{
	/**
	 * Name of the context attribute
	 * @return Name of the context attribute
	 */
	public String value();
}
