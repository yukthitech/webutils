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

package com.yukthi.webutils.common.models;

import java.util.List;

/**
 * Response model for execute search action
 * @author akiran
 */
public class ExecuteSearchResponse extends BaseResponse
{
	/**
	 * Search results
	 */
	private List<Object> searchResults;
	
	/**
	 * Instantiates a new execute search response.
	 */
	public ExecuteSearchResponse()
	{}

	/**
	 * Instantiates a new execute search response.
	 *
	 * @param searchResults the search results
	 */
	public ExecuteSearchResponse(List<Object> searchResults)
	{
		this.searchResults = searchResults;
	}

	/**
	 * Gets the search results.
	 *
	 * @return the search results
	 */
	public List<Object> getSearchResults()
	{
		return searchResults;
	}

	/**
	 * Sets the search results.
	 *
	 * @param searchResults the new search results
	 */
	public void setSearchResults(List<Object> searchResults)
	{
		this.searchResults = searchResults;
	}
}
