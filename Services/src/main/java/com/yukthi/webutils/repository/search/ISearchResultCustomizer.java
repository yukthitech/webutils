package com.yukthi.webutils.repository.search;

import java.util.List;

/**
 * The Interface ISearchResultCustomizer Contains List of SearchResults. the
 * method can be invoked for customizing search results.
 * 
 * @param <T>
 *            the generic type
 */
public interface ISearchResultCustomizer<T>
{

	/**
	 * Customizer.
	 *
	 * @param results
	 *            the results
	 * @return the list
	 */
	public List<T> customizer(List<T> results);
}
