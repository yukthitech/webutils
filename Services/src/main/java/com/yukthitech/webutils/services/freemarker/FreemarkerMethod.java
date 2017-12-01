package com.yukthitech.webutils.services.freemarker;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark a method as free marker expression method.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FreemarkerMethod
{
	/**
	 * Name for this method that will be used in free marker templates.
	 * Defaults to method name.
	 * @return Name of the method
	 */
	public String value() default "";
}
