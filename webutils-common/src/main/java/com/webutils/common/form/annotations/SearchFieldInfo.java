package com.webutils.common.form.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.webutils.common.search.SearchResultType;

/**
 * Defines default order and rendering type for search result columns.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SearchFieldInfo
{
	int order() default -1;

	SearchResultType resultType() default SearchResultType.NONE;
}
