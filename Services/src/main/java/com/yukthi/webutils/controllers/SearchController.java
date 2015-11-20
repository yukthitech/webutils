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

package com.yukthi.webutils.controllers;

import static com.yukthi.webutils.common.IWebUtilsActionConstants.ACTION_PREFIX_SEARCH;
import static com.yukthi.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_EXECUTE;
import static com.yukthi.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_FETCH_QUERY_DEF;
import static com.yukthi.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_FETCH_RESULT_DEF;
import static com.yukthi.webutils.common.IWebUtilsActionConstants.PARAM_NAME;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthi.webutils.InvalidRequestParameterException;
import com.yukthi.webutils.annotations.ActionName;
import com.yukthi.webutils.common.IWebUtilsCommonConstants;
import com.yukthi.webutils.common.SearchExecutionModel;
import com.yukthi.webutils.common.models.ExecuteSearchResponse;
import com.yukthi.webutils.common.models.ModelDefResponse;
import com.yukthi.webutils.services.SearchService;

/**
 * Controller for fetching LOV values.
 * @author akiran
 */
@RestController
@ActionName(ACTION_PREFIX_SEARCH)
@RequestMapping("/search")
public class SearchController extends BaseController
{
	private static Logger logger = LogManager.getLogger(SearchController.class);
	
	@Autowired
	private SearchService searchService;
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	{
		objectMapper.setDateFormat(IWebUtilsCommonConstants.DEFAULT_DATE_FORMAT);
	}
	
	/**
	 * Used to fetch query definition for specified query
	 * @param queryName Query name for which query def needs to be fetched
	 * @return Query object definition
	 */
	@ActionName(ACTION_TYPE_FETCH_QUERY_DEF)
	@ResponseBody
	@RequestMapping(value = "/fetch/{" + PARAM_NAME + "}/query/def", method = RequestMethod.GET)
	public ModelDefResponse fetchSearchQueryDef(@PathVariable(PARAM_NAME) String queryName)
	{
		logger.trace("fetchSearchQueryDef is called for query - {}", queryName);
		
		return new ModelDefResponse( searchService.getSearhQueryDefinition(queryName) );
	}

	/**
	 * Used to fetch query result definitions for specified query
	 * @param queryName Query for which query result def needs to be fetched
	 * @return Query result definition
	 */
	@ActionName(ACTION_TYPE_FETCH_RESULT_DEF)
	@ResponseBody
	@RequestMapping(value = "/fetch/{" + PARAM_NAME + "}/execute", method = RequestMethod.GET)
	public ModelDefResponse fetchSearchResultDef(@PathVariable(PARAM_NAME) String queryName)
	{
		logger.trace("fetchSearchResultDef is called for query - {}", queryName);
		
		return new ModelDefResponse( searchService.getSearhResultDefinition(queryName) );
	}
	
	/**
	 * Executes specified search query with query object
	 * @param queryName Name of the query to execute
	 * @param searchExecutionModel Query object
	 * @return List of search results
	 */
	@ActionName(ACTION_TYPE_EXECUTE)
	@ResponseBody
	@RequestMapping(value = "/execute/{" + PARAM_NAME + "}", method = RequestMethod.GET)
	public ExecuteSearchResponse executeSearch(@PathVariable(PARAM_NAME) String queryName, @Valid SearchExecutionModel searchExecutionModel)
	{
		logger.trace("executeSearch is called for query - {}", queryName);
		
		Class<?> queryType = searchService.getSearchQueryType(queryName);
		Object query = null;
		
		if(searchExecutionModel.getQueryModelJson() != null)
		{
			try
			{
				query = objectMapper.readValue(searchExecutionModel.getQueryModelJson(), queryType);
			}catch(Exception ex)
			{
				throw new InvalidRequestParameterException(ex, "Failed to convert input json to {}. Input json - ", queryType.getName(), searchExecutionModel.getQueryModelJson());
			}
		}
		
		return new ExecuteSearchResponse( searchService.executeSearch(queryName, query, searchExecutionModel.getPageSize()) );
	}
}
