package com.yukthitech.webutils.common.search;

import java.util.List;

import com.yukthitech.webutils.common.models.BaseResponse;

/**
 * Represents the search response.
 * @author akiran
 */
public class SearchResponse extends BaseResponse
{
	/**
	 * results of search.
	 */
	private List<Object> results;
	
	/**
	 * Instantiates a new search response.
	 */
	public SearchResponse()
	{
	}

	/**
	 * Instantiates a new search response.
	 *
	 * @param results the results
	 */
	public SearchResponse(List<Object> results)
	{
		this.results = results;
	}

	/**
	 * Gets the results of search.
	 *
	 * @return the results of search
	 */
	public List<Object> getResults()
	{
		return results;
	}

	/**
	 * Sets the results of search.
	 *
	 * @param results the new results of search
	 */
	public void setResults(List<Object> results)
	{
		this.results = results;
	}
}
