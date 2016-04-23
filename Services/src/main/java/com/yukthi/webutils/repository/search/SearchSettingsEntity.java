package com.yukthi.webutils.repository.search;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.yukthi.persistence.annotations.DataType;
import com.yukthi.persistence.annotations.DataTypeMapping;
import com.yukthi.persistence.annotations.NotUpdateable;
import com.yukthi.persistence.annotations.UniqueConstraint;
import com.yukthi.persistence.annotations.UniqueConstraints;
import com.yukthi.persistence.conversion.impl.JsonConverter;
import com.yukthi.webutils.common.models.search.SearchSettingsColumn;
import com.yukthi.webutils.repository.UserEntity;
import com.yukthi.webutils.repository.WebutilsEntity;

/**
 * Search query customization settings.
 */
@Table(name = "SEARCH_SETTINGS")
@UniqueConstraints({
	@UniqueConstraint(name = "UQ_USER_QUERY_NAME", fields = {"user", "searchQueryName"})
	})
public class SearchSettingsEntity extends WebutilsEntity
{
	/**
	 * User for which this setting is being created.
	 */
	@NotUpdateable
	@ManyToOne
	@Column(name = "USER_ID", nullable = false)
	private UserEntity user;
	
	/**
	 * Search query name for which setting is being maintained.
	 */
	@NotUpdateable
	@Column(name = "SEARCH_QUERY_NAME", nullable = false, length = 100)
	private String searchQueryName;
	
	/**
	 * Search column order with display flags.
	 */
	@Column(name = "SEARCH_COLUMNS", nullable = false, length = 4000)
	@DataTypeMapping(type = DataType.STRING, converterType = JsonConverter.class)
	private List<SearchSettingsColumn> searchColumns;
	
	/**
	 * Search results page size.
	 */
	@Column(name = "PAGE_SIZE", nullable = false)
	private Integer pageSize;
	
	/**
	 * Instantiates a new search settings entity.
	 */
	public SearchSettingsEntity()
	{
	}

	/**
	 * Gets the user for which this setting is being created.
	 *
	 * @return the user for which this setting is being created
	 */
	public UserEntity getUser()
	{
		return user;
	}

	/**
	 * Sets the user for which this setting is being created.
	 *
	 * @param user the new user for which this setting is being created
	 */
	public void setUser(UserEntity user)
	{
		this.user = user;
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
	 * Sets the search results page size.
	 *
	 * @param pageSize the new search results page size
	 */
	public void setPageSize(Integer pageSize)
	{
		this.pageSize = pageSize;
	}

	/**
	 * Gets the search results page size.
	 *
	 * @return the search results page size
	 */
	public Integer getPageSize()
	{
		return pageSize;
	}
}
