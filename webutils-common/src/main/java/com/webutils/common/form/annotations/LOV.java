package com.webutils.common.form.annotations;

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
	
	/**
	 * Type of lov being referred.
	 * @return
	 */
	public DynLovType type() default DynLovType.DYNAMIC_TYPE;
	
	/**
	 * Controls whether new typed values are allowed on String editable / multi-editable LOVs.
	 * <p>
	 * When {@code false}:
	 * <ul>
	 * <li>UI create/tagging is disabled (select existing options only)</li>
	 * <li>Server {@code processModel} will not create new {@code STORED_LOV_OPTION} rows</li>
	 * </ul>
	 * Use for search filters and other select-only String LOV fields.
	 *
	 * @return whether new LOV options may be created
	 */
	public boolean persist() default true;
}
