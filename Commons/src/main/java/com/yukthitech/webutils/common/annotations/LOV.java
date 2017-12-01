package com.yukthitech.webutils.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark a field as LOV type (which maps to dynamic LOV)
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface LOV
{
	/**
	 * Name of the dynamic LOV
	 * @return name of the dynamic LOV
	 */
	public String name();
	
	/**
	 * Parent field of the LOV. If defined, the LOV values for the field will be based on the
	 * parent field value.
	 * @return Parent field name, if any
	 */
	public String parentField() default "";
}
