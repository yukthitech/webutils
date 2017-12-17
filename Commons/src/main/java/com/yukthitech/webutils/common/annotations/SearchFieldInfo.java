package com.yukthitech.webutils.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines the order of the field. One usage is in search result models, this annotation can be used to 
 * specify default order of search result columns.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SearchFieldInfo
{
	/**
	 * Order of the field.
	 * @return order of the field.
	 */
	public int order() default -1;

	/**
	 * Provides special type of search field.
	 * @return search field type.
	 */
	public SearchResultType resultType() default SearchResultType.NONE;
}
