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

package com.yukthitech.webutils.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.webutils.common.annotations.Model;

import jakarta.validation.constraints.Min;

/**
 * Model used to execute search queries.
 * @author akiran
 */
@Model
public class SearchExecutionModel
{
	/**
	 * Object mapper to convert to json.
	 */
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	/**
	 * Query object json.
	 */
	private String queryModelJson;
	
	/**
	 * Page number to be fetched. If less than or equals to zero all records will be fetched.
	 */
	private int pageSize = -1;
	
	/**
	 * Page to be fetched. 
	 */
	@Min(1)
	private int pageNumber = 1;
	
	/**
	 * If true, along with search results (paged), the total number of records will be fetched.
	 */
	private boolean fetchCount = false;
	
	/**
	 * For internal use. Indicates all records needs to be fetched (used by export).
	 */
	private boolean fetchAll = false;
	
	/**
	 * Instantiates a new search execution model.
	 */
	public SearchExecutionModel()
	{}

	/**
	 * Instantiates a new search execution model.
	 *
	 * @param pageNumber the page number
	 * @param fetchCount the fetch count
	 * @param fetchAll the fetch all
	 */
	public SearchExecutionModel(int pageNumber, boolean fetchCount, boolean fetchAll)
	{
		this.pageNumber = pageNumber;
		this.fetchCount = fetchCount;
		this.fetchAll = fetchAll;
	}

	/**
	 * Gets the query object json.
	 *
	 * @return the query object json
	 */
	public String getQueryModelJson()
	{
		return queryModelJson;
	}

	/**
	 * Sets the query object json.
	 *
	 * @param queryModelJson the new query object json
	 */
	public void setQueryModelJson(String queryModelJson)
	{
		this.queryModelJson = queryModelJson;
	}
	
	/**
	 * Sets specified model as json on this model.
	 * @param model model to set as query json
	 */
	public void setQueryModel(Object model)
	{
		try
		{
			this.queryModelJson = objectMapper.writeValueAsString(model);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while converting specified object into json", ex);
		}
	}

	/**
	 * Gets the page number to be fetched.
	 *
	 * @return the page number to be fetched
	 */
	public int getPageSize()
	{
		return pageSize;
	}

	/**
	 * Sets the page number to be fetched.
	 *
	 * @param pageSize the new page number to be fetched
	 */
	public void setPageSize(int pageSize)
	{
		this.pageSize = pageSize;
	}

	/**
	 * Gets the page to be fetched.
	 *
	 * @return the page to be fetched
	 */
	public int getPageNumber()
	{
		return pageNumber;
	}

	/**
	 * Sets the page to be fetched.
	 *
	 * @param pageNumber the new page to be fetched
	 */
	public void setPageNumber(int pageNumber)
	{
		this.pageNumber = pageNumber;
	}

	/**
	 * Checks if is if true, along with search results (paged), the total number of records will be fetched.
	 *
	 * @return the if true, along with search results (paged), the total number of records will be fetched
	 */
	public boolean isFetchCount()
	{
		return fetchCount;
	}

	/**
	 * Sets the if true, along with search results (paged), the total number of records will be fetched.
	 *
	 * @param fetchCount the new if true, along with search results (paged), the total number of records will be fetched
	 */
	public void setFetchCount(boolean fetchCount)
	{
		this.fetchCount = fetchCount;
	}

	/**
	 * Checks if is for internal use. Indicates all records needs to be fetched (used by export).
	 *
	 * @return the for internal use
	 */
	@JsonIgnore
	public boolean isFetchAll()
	{
		return fetchAll;
	}

	/**
	 * Sets the for internal use. Indicates all records needs to be fetched (used by export).
	 *
	 * @param fetchAll the new for internal use
	 */
	public void setFetchAll(boolean fetchAll)
	{
		this.fetchAll = fetchAll;
	}
}
