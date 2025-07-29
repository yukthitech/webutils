package com.yukthitech.webutils.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.yukthitech.webutils.common.lov.DynLovType;

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
	
	/**
	 * Type of lov being referred.
	 * @return
	 */
	public DynLovType type() default DynLovType.DYNAMIC_TYPE;
	
	/**
	 * Used to mark editable lov as not-persist for new options.
	 * 
	 * This can be set to 'false' on editable-lov (which is getting converted to string), which can take help of existing lov values.
	 * However the new items will not be persisted as lov options, if this flag is set to false.
	 * 
	 * @return
	 */
	public boolean persist() default true;
}
