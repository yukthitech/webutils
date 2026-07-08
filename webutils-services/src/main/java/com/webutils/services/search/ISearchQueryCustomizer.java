package com.webutils.services.search;

/**
 * Per-query hook to customize the search query POJO before execution.
 */
public interface ISearchQueryCustomizer
{
	void customizeQuery(SearchCustomizationContext context);
}
