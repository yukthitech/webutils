package com.yukthitech.webutils.common.search;

import com.yukthitech.webutils.common.annotations.SearchResultType;
import com.yukthitech.webutils.common.models.def.FieldType;

/**
 * Provides details about search column in search results.
 * @author akiran
 */
public class SearchColumn
{
	/**
	 * Name of the search result field.
	 */
	private String name;
	
	/**
	 * Heading to be used for this column.
	 */
	private String heading;
	
	/**
	 * Indicates whether this field can be displayed or not.
	 */
	private boolean displayable;
	
	/**
	 * Data type of the field.
	 */
	private FieldType type;
	
	/**
	 * Search result type.
	 */
	private SearchResultType searchResultType;
	
	/**
	 * Instantiates a new search column.
	 */
	public SearchColumn()
	{}

	/**
	 * Instantiates a new search column.
	 *
	 * @param name the name
	 * @param heading the heading
	 * @param displayable the displayable
	 * @param type the type
	 * @param searchResultType Search result type
	 */
	public SearchColumn(String name, String heading, boolean displayable, FieldType type, SearchResultType searchResultType)
	{
		this.name = name;
		this.heading = heading;
		this.displayable = displayable;
		this.type = type;
		this.searchResultType = searchResultType;
	}

	/**
	 * Gets the heading to be used for this column.
	 *
	 * @return the heading to be used for this column
	 */
	public String getHeading()
	{
		return heading;
	}

	/**
	 * Sets the heading to be used for this column.
	 *
	 * @param heading the new heading to be used for this column
	 */
	public void setHeading(String heading)
	{
		this.heading = heading;
	}

	/**
	 * Gets the indicates whether this field can be displayed or not.
	 *
	 * @return the indicates whether this field can be displayed or not
	 */
	public boolean isDisplayable()
	{
		return displayable;
	}

	/**
	 * Sets the indicates whether this field can be displayed or not.
	 *
	 * @param displayable the new indicates whether this field can be displayed or not
	 */
	public void setDisplayable(boolean displayable)
	{
		this.displayable = displayable;
	}

	/**
	 * Gets the name of the search result field.
	 *
	 * @return the name of the search result field
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the search result field.
	 *
	 * @param name the new name of the search result field
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the data type of the field.
	 *
	 * @return the data type of the field
	 */
	public FieldType getType()
	{
		return type;
	}

	/**
	 * Sets the data type of the field.
	 *
	 * @param type the new data type of the field
	 */
	public void setType(FieldType type)
	{
		this.type = type;
	}

	/**
	 * Gets the search result type.
	 *
	 * @return the search result type
	 */
	public SearchResultType getSearchResultType()
	{
		return searchResultType;
	}

	/**
	 * Sets the search result type.
	 *
	 * @param searchResultType the new search result type
	 */
	public void setSearchResultType(SearchResultType searchResultType)
	{
		this.searchResultType = searchResultType;
	}
}
