package com.webutils.common.form.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to define default value for a model field.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DefaultValue
{
	/**
	 * Default value for the field.
	 * @return Default value for the field
	 */
	public String value() default "";
	
	/**
	 * Resource text file from which default value should be fetched for this field.
	 * @return resource path.
	 */
	public String resource() default "";
}
