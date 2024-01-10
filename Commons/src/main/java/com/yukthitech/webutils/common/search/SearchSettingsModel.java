package com.yukthitech.webutils.common.search;

import java.util.ArrayList;
import java.util.List;

import com.yukthitech.validation.annotations.MinLen;
import com.yukthitech.validation.annotations.Required;
import com.yukthitech.webutils.common.annotations.IgnoreField;
import com.yukthitech.webutils.common.annotations.Model;

import jakarta.validation.constraints.Min;

/**
 * Search query customization settings.
 */
@Model
public class SearchSettingsModel
{
	/**
	 * Id of search settings if already exists.
	 */
	private Long id;
	
	/**
	 * Version of the settings.
	 */
	private Integer version;
	
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
	private int pageSize;
	
	/**
	 * Instantiates a new search settings entity.
	 */
	public SearchSettingsModel()
	{
	}
	
	/**
	 * Gets the id of search settings if already exists.
	 *
	 * @return the id of search settings if already exists
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * Sets the id of search settings if already exists.
	 *
	 * @param id the new id of search settings if already exists
	 */
	public void setId(Long id)
	{
		this.id = id;
	}
	
	/**
	 * Gets the version of the settings;.
	 *
	 * @return the version of the settings;
	 */
	public Integer getVersion()
	{
		return version;
	}

	/**
	 * Sets the version of the settings;.
	 *
	 * @param version the new version of the settings;
	 */
	public void setVersion(Integer version)
	{
		this.version = version;
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
	 * Adds specified search column details to current settings.
	 * @param column Column to add.
	 */
	public void addSearchColumn(SearchSettingsColumn column)
	{
		if(this.searchColumns == null)
		{
			this.searchColumns = new ArrayList<>();
		}
		
		this.searchColumns.add(column);
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
