package com.yukthi.webutils.common;

import com.yukthi.persistence.repository.search.IDynamicSearchResult;

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
