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

package com.yukthi.webutils.common;

import com.yukthi.webutils.common.annotations.Model;

/**
 * Model used to execute search queries.
 * @author akiran
 */
@Model
public class SearchExecutionModel
{
	/**
	 * Query object json.
	 */
	private String queryModelJson;
	
	/**
	 * Results page size.
	 */
	private int pageSize = -1;
	
	/**
	 * Page to be fetched.
	 */
	private int pageNumber = 0;

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
	 * Gets the results page size.
	 *
	 * @return the results page size
	 */
	public int getPageSize()
	{
		return pageSize;
	}

	/**
	 * Sets the results page size.
	 *
	 * @param pageSize the new results page size
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
}
