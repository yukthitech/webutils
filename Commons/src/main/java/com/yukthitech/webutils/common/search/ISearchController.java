package com.yukthitech.webutils.common.search;

import com.yukthitech.webutils.common.RemoteService;
import com.yukthitech.webutils.common.SearchExecutionModel;
import com.yukthitech.webutils.common.controllers.IClientController;
import com.yukthitech.webutils.common.models.ModelDefResponse;

@RemoteService
public interface ISearchController extends IClientController<ISearchController>
{
	/**
	 * Used to fetch query definition for specified query.
	 * @param queryName Query name for which query def needs to be fetched
	 * @return Query object definition
	 */
	ModelDefResponse fetchSearchQueryDef(String queryName);

	/**
	 * Used to fetch query result definitions for specified query.
	 * @param queryName Query for which query result def needs to be fetched
	 * @return Query result definition
	 */
	ModelDefResponse fetchSearchResultDef(String queryName);

	/**
	 * Executes specified search query and returns results in flat table structure.
	 * @param queryName Name of the query to execute
	 * @param searchExecutionModel Query object
	 * @return List of search results in generic format.
	 */
	ExecuteSearchResponse executeSearch(String queryName, SearchExecutionModel searchExecutionModel) throws Exception;
	
	/**
	 * Executes specified search query with query object. Instead of returning flat table structure, search result
	 * objects will be returned.
	 * @param queryName Name of the query to execute
	 * @param searchExecutionModel Query object
	 * @return List of search results as objects.
	 */
	SearchResponse executeSearchObjects(String queryName, SearchExecutionModel searchExecutionModel) throws Exception;

	void exportSearch(String queryName, SearchExecutionModel searchExecutionModel) throws Exception;

}