package com.yukthi.webutils.common.models;

import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.yukthi.validation.annotations.MinLen;
import com.yukthi.validation.annotations.Required;
import com.yukthi.webutils.common.annotations.IgnoreField;
import com.yukthi.webutils.common.annotations.Model;
import com.yukthi.webutils.common.models.search.SearchSettingsColumn;

/**
 * Search query customization settings.
 */
@Model
public class SearchSettingsModel
{
	/**
	 * Search query name for which setting is being maintained.
	 */
	@Required
	@MinLen(1)
	private String searchQueryName;
	
	/**
	 * Search column order with display flags.
	 */
	@IgnoreField
	@Required
	private List<SearchSettingsColumn> searchColumns;
	
	/**
	 * Search results page size.
	 */
	@Min(1)
	@Max(100)
	private int pageSize;
	
	/**
	 * Instantiates a new search settings entity.
	 */
	public SearchSettingsModel()
	{
	}
	
	/**
	 * Gets the search query name for which setting is being maintained.
	 *
	 * @return the search query name for which setting is being maintained
	 */
	public String getSearchQueryName()
	{
		return searchQueryName;
	}

	/**
	 * Sets the search query name for which setting is being maintained.
	 *
	 * @param searchQueryName the new search query name for which setting is being maintained
	 */
	public void setSearchQueryName(String searchQueryName)
	{
		this.searchQueryName = searchQueryName;
	}

	/**
	 * Gets the search column order with display flags.
	 *
	 * @return the search column order with display flags
	 */
	public List<SearchSettingsColumn> getSearchColumns()
	{
		return searchColumns;
	}

	/**
	 * Sets the search column order with display flags.
	 *
	 * @param searchColumns the new search column order with display flags
	 */
	public void setSearchColumns(List<SearchSettingsColumn> searchColumns)
	{
		this.searchColumns = searchColumns;
	}

	/**
	 * Gets the search results page size.
	 *
	 * @return the search results page size
	 */
	public int getPageSize()
	{
		return pageSize;
	}

	/**
	 * Sets the search results page size.
	 *
	 * @param pageSize the new search results page size
	 */
	public void setPageSize(int pageSize)
	{
		this.pageSize = pageSize;
	}
}
