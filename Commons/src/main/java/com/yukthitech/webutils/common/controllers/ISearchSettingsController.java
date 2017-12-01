package com.yukthitech.webutils.common.controllers;

import com.yukthitech.webutils.common.RemoteService;
import com.yukthitech.webutils.common.models.BaseResponse;
import com.yukthitech.webutils.common.models.BasicReadResponse;
import com.yukthitech.webutils.common.models.BasicSaveResponse;
import com.yukthitech.webutils.common.models.SearchSettingsModel;

/**
 * Controller interface for search settings.
 * @author akiran
 */
@RemoteService
public interface ISearchSettingsController extends IClientController<ISearchSettingsController>
{
	/**
	 * Saves specified search setting for user.
	 * @param model Search setting to be saved.
	 * @return Save response
	 */
	BasicSaveResponse save(SearchSettingsModel model);

	/**
	 * Updates specified search settings for specified user.
	 * @param model Search settings to be saved.
	 * @return Update response.
	 */
	BaseResponse update(SearchSettingsModel model);

	/**
	 * Fetches search setting for current user.
	 * @param searchQueryName Search query name
	 * @return Matching search query settings.
	 */
	BasicReadResponse<SearchSettingsModel> fetch(String searchQueryName);

	/**
	 * Deletes the settings specified id.
	 * @param searchQueryName Name of the query to be deleted.
	 * @return Delete response.
	 */
	BaseResponse delete(String searchQueryName);

	/**
	 * Deletes all search query settings. For testing purpose only.
	 * @return Delete response.
	 */
	BaseResponse deleteAll();
}