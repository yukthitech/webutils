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

package com.yukthi.webutils.client.helpers;

import static com.yukthi.webutils.common.IWebUtilsActionConstants.ACTION_PREFIX_SEARCH;
import static com.yukthi.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_EXECUTE;
import static com.yukthi.webutils.common.IWebUtilsActionConstants.PARAM_NAME;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthi.utils.CommonUtils;
import com.yukthi.utils.exceptions.InvalidStateException;
import com.yukthi.utils.rest.RestClient;
import com.yukthi.utils.rest.RestRequest;
import com.yukthi.utils.rest.RestResult;
import com.yukthi.webutils.client.ActionRequestBuilder;
import com.yukthi.webutils.client.ClientContext;
import com.yukthi.webutils.client.RestException;
import com.yukthi.webutils.common.IWebUtilsCommonConstants;
import com.yukthi.webutils.common.SearchExecutionModel;
import com.yukthi.webutils.common.models.search.ExecuteSearchResponse;

/**
 * Helper to execute search related functions
 * @author akiran
 */
public class SearchHelper
{
	private ObjectMapper objectMapper = new ObjectMapper();
	
	{
		objectMapper.setDateFormat(IWebUtilsCommonConstants.DEFAULT_DATE_FORMAT);
	}

	/**
	 * Executes search query with specified query object
	 * @param context Client context
	 * @param queryName Name of query to execute
	 * @param searchQuery Query object
	 * @param pageSize Query page size
	 * @return List of search results
	 */
	public ExecuteSearchResponse executeSearchQuery(ClientContext context, String queryName, Object searchQuery, int pageSize)
	{
		//Build model object
		SearchExecutionModel searchExecutionModel = new SearchExecutionModel();
		searchExecutionModel.setPageSize(pageSize);
		
		try
		{
			searchExecutionModel.setQueryModelJson(searchQuery == null ? null : objectMapper.writeValueAsString(searchQuery));
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while converting {} into json", searchQuery);
		}
		
		//build request object
		RestRequest<?> request = ActionRequestBuilder.buildRequest(context, ACTION_PREFIX_SEARCH + "." + ACTION_TYPE_EXECUTE, searchExecutionModel, CommonUtils.toMap(
				PARAM_NAME, queryName
		));
		
		RestClient client = context.getRestClient();
		
		//execute request
		RestResult<ExecuteSearchResponse> searchResult = client.invokeJsonRequest(request, ExecuteSearchResponse.class);
		ExecuteSearchResponse response = searchResult.getValue();
		
		if(response == null || response.getCode() != 0)
		{
			throw new RestException("An error occurred while executing search-query - " + queryName, searchResult.getStatusCode(), response);
		}
		
		return response;
	}
}
