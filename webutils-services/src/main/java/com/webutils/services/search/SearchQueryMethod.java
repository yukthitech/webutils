package com.webutils.services.search;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.yukthitech.persistence.repository.annotations.SearchFunction;
import com.yukthitech.persistence.repository.annotations.SearchResult;

/**
 * Marks a repository method as a searchable query.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@SearchResult
@SearchFunction
public @interface SearchQueryMethod
{
	String name();

	Class<?> queryModel();

	Class<? extends ISearchQueryCustomizer> queryCustomizer() default ISearchQueryCustomizer.class;

	Class<? extends ISearchResultCustomizer> resultCustomizer() default ISearchResultCustomizer.class;
}
