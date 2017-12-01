package com.yukthitech.webutils.repository.search;

import java.util.List;

/**
 * The Interface ISearchResultCustomizer method can be used to 
 * customize the searchResults.
 *
 * @param <T> the generic type
 */
public interface ISearchResultCustomizer<T>
{
	/**
	 * Customizer.
	 *
	 * @param results the results
	 * @return the list
	 */
	public List<T> customize(List<T> results);
}
