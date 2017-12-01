package com.yukthitech.webutils.common;

import com.yukthitech.persistence.repository.search.IDynamicSearchResult;

/**
 * Abstraction for search results which can hold extended search result fields.
 * @author akiran
 */
public interface IExtendedSearchResult extends IDynamicSearchResult
{
	/**
	 * Fetches value of the dynamic field.
	 * @param name Name of the field.
	 * @return Field value
	 */
	public Object getDynamicFieldValue(String name);
}
