package com.webutils.services.search;

import java.util.List;

/**
 * Per-query hook to customize search results after execution.
 */
public interface ISearchResultCustomizer
{
	default List<?> customize(SearchCustomizationContext context, List<?> results)
	{
		return results;
	}
}
