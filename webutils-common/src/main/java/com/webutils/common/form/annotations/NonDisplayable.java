package com.webutils.common.form.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark a field as non-displayable field
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NonDisplayable
{
	/**
	 * Used to mark the field as backend key. This is helpful
	 * to indicate that though field is not displayable it is a 
	 * backend key.
	 * @return true if this is backend field.
	 */
	public boolean backend() default false;
}
