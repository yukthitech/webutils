package com.yukthitech.webutils.common.controllers;

import com.yukthitech.webutils.common.RemoteService;
import com.yukthitech.webutils.common.SearchExecutionModel;
import com.yukthitech.webutils.common.models.ModelDefResponse;
import com.yukthitech.webutils.common.models.search.ExecuteSearchResponse;

@RemoteService
public interface ISearchController extends IClientController<ISearchController>
{

	/**
	 * Used to fetch query definition for specified query
	 * @param queryName Query name for which query def needs to be fetched
	 * @return Query object definition
	 */
	ModelDefResponse fetchSearchQueryDef(String queryName);

	/**
	 * Used to fetch query result definitions for specified query
	 * @param queryName Query for which query result def needs to be fetched
	 * @return Query result definition
	 */
	ModelDefResponse fetchSearchResultDef(String queryName);

	/**
	 * Executes specified search query with query object
	 * @param queryName Name of the query to execute
	 * @param searchExecutionModel Query object
	 * @return List of search results
	 */
	ExecuteSearchResponse executeSearch(String queryName, SearchExecutionModel searchExecutionModel) throws Exception;

	void exportSearch(String queryName, SearchExecutionModel searchExecutionModel) throws Exception;

}