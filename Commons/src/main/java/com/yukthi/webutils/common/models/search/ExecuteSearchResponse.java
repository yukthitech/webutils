/*
 * The MIT License (MIT)
 * Copyright (c) 2015 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.yukthi.webutils.common.models.search;

import java.util.ArrayList;
import java.util.List;

import com.yukthi.webutils.common.models.BaseResponse;

/**
 * Response model for execute search action.
 * @author akiran
 */
public class ExecuteSearchResponse extends BaseResponse
{
	/**
	 * List of search column details of the search results.
	 */
	private List<SearchColumn> searchColumns;
	
	/**
	 * Search results.
	 */
	private List<SearchRow> searchResults;
	
	/**
	 * Page number of the search results.
	 */
	private int pageNumber;
	
	/**
	 * Total number of records.
	 */
	private long totalCount;
	
	/**
	 * Instantiates a new execute search response.
	 */
	public ExecuteSearchResponse()
	{}

	/**
	 * Gets the list of search column details of the search results.
	 *
	 * @return the list of search column details of the search results
	 */
	public List<SearchColumn> getSearchColumns()
	{
		return searchColumns;
	}

	/**
	 * Sets the list of search column details of the search results.
	 *
	 * @param searchColumns the new list of search column details of the search results
	 */
	public void setSearchColumns(List<SearchColumn> searchColumns)
	{
		this.searchColumns = searchColumns;
	}
	
	/**
	 * Adds specified search column.
	 * @param searchColumn Search column to be added.
	 */
	public void addSearchColumn(SearchColumn searchColumn)
	{
		if(this.searchColumns == null)
		{
			this.searchColumns = new ArrayList<>();
		}
		
		this.searchColumns.add(searchColumn);
	}

	/**
	 * Gets the search results.
	 *
	 * @return the search results
	 */
	public List<SearchRow> getSearchResults()
	{
		return searchResults;
	}

	/**
	 * Sets the search results.
	 *
	 * @param searchResults the new search results
	 */
	public void setSearchResults(List<SearchRow> searchResults)
	{
		this.searchResults = searchResults;
	}
	
	/**
	 * Adds specified search result.
	 * @param row Search result to be added.
	 */
	public void addSearchResult(SearchRow row)
	{
		if(this.searchResults == null)
		{
			this.searchResults = new ArrayList<>();
		}
		
		this.searchResults.add(row);
	}

	/**
	 * Gets the page number of the search results.
	 *
	 * @return the page number of the search results
	 */
	public int getPageNumber()
	{
		return pageNumber;
	}

	/**
	 * Sets the page number of the search results.
	 *
	 * @param pageNumber the new page number of the search results
	 */
	public void setPageNumber(int pageNumber)
	{
		this.pageNumber = pageNumber;
	}

	/**
	 * Gets the total number of records.
	 *
	 * @return the total number of records
	 */
	public long getTotalCount()
	{
		return totalCount;
	}

	/**
	 * Sets the total number of records.
	 *
	 * @param totalCount the new total number of records
	 */
	public void setTotalCount(long totalCount)
	{
		this.totalCount = totalCount;
	}
}
