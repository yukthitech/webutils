package com.yukthitech.webutils.repository.search;

import java.util.List;

import com.yukthitech.webutils.common.FileInfo;
import com.yukthitech.webutils.common.search.ExecuteSearchResponse;
import com.yukthitech.webutils.search.SearchCustomizationContext;

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
	public default List<T> customize(SearchCustomizationContext context, List<T> results)
	{
		return results;
	}

	/**
	 * Can be used to customize the file being exported and result in entire new file.
	 * @param results
	 * @param resultFile
	 * @return
	 */
	public default FileInfo customizeExportingFile(SearchCustomizationContext context, ExecuteSearchResponse results, FileInfo resultFile)
	{
		return resultFile;
	}
}
