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

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.yukthi.webutils.InvalidRequestParameterException;
import com.yukthi.webutils.annotations.ActionName;
import com.yukthi.webutils.common.controllers.ISearchSettingsController;
import com.yukthi.webutils.common.models.BaseResponse;
import com.yukthi.webutils.common.models.BasicReadResponse;
import com.yukthi.webutils.common.models.BasicSaveResponse;
import com.yukthi.webutils.common.models.SearchSettingsModel;
import com.yukthi.webutils.repository.search.SearchSettingsEntity;
import com.yukthi.webutils.services.SearchService;
import com.yukthi.webutils.services.SearchSettingsService;

/**
 * Search settings controller.
 * @author akiran
 */
@RestController
@RequestMapping("/searchSettings")
@ActionName("searchSettings")
public class SearchSettingsController extends BaseController implements ISearchSettingsController
{
	/**
	 * Service for managing search settings.
	 */
	@Autowired
	private SearchSettingsService service;
	
	/**
	 * Search service used to validate search query name.
	 */
	@Autowired
	private SearchService searchService;

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.common.controllers.ISearchSettingsController#save(com.yukthi.webutils.common.models.SearchSettingsModel)
	 */
	@Override
	@ResponseBody
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ActionName("save")
	public BasicSaveResponse save(@Valid @RequestBody SearchSettingsModel model)
	{
		String searchQueryName = model.getSearchQueryName();
		
		//ensure search query name specified is valid
		if(searchService.getSearchQueryType(searchQueryName) == null)
		{
			throw new InvalidRequestParameterException("Invalid search query name specified - {}", searchQueryName);
		}
		
		SearchSettingsEntity entity = service.save(model);
		return new BasicSaveResponse(entity.getId());
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.common.controllers.ISearchSettingsController#update(com.yukthi.webutils.common.models.SearchSettingsModel)
	 */
	@Override
	@ResponseBody
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@ActionName("update")
	public BaseResponse update(@Valid @RequestBody SearchSettingsModel model)
	{
		String searchQueryName = model.getSearchQueryName();
		
		//ensure search query name specified is valid
		if(searchService.getSearchQueryType(searchQueryName) == null)
		{
			throw new InvalidRequestParameterException("Invalid search query name specified - {}", searchQueryName);
		}
		
		service.update(model);
		return new BaseResponse();
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.common.controllers.ISearchSettingsController#fetch(java.lang.String)
	 */
	@ResponseBody
	@RequestMapping(value = "/read/{queryName}", method = RequestMethod.GET)
	@ActionName("read")
	@Override
	public BasicReadResponse<SearchSettingsModel> fetch(@PathVariable("queryName") String searchQueryName)
	{
		return new BasicReadResponse<SearchSettingsModel>(service.fetch(searchQueryName));
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.common.controllers.ISearchSettingsController#delete(long)
	 */
	@ResponseBody
	@RequestMapping(value = "/delete/{queryName}", method = RequestMethod.DELETE)
	@ActionName("delete")
	@Override
	public BaseResponse delete(@PathVariable("queryName") String searchQueryName)
	{
		service.deleteByName(searchQueryName);
		return new BaseResponse();
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.common.controllers.ISearchSettingsController#deleteAll()
	 */
	@ResponseBody
	@RequestMapping(value = "/deleteAll", method = RequestMethod.DELETE)
	@ActionName("deleteAll")
	@Override
	public BaseResponse deleteAll()
	{
		service.deleteAll();
		return new BaseResponse();
	}
}
