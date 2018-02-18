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

package com.yukthitech.webutils.search;

import static com.yukthitech.webutils.common.IWebUtilsActionConstants.ACTION_PREFIX_SEARCH;
import static com.yukthitech.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_EXECUTE;
import static com.yukthitech.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_EXPORT;
import static com.yukthitech.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_FETCH_QUERY_DEF;
import static com.yukthitech.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_FETCH_RESULT_DEF;
import static com.yukthitech.webutils.common.IWebUtilsActionConstants.PARAM_NAME;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
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
import com.yukthitech.excel.exporter.ExcelExporter;
import com.yukthitech.webutils.InvalidRequestParameterException;
import com.yukthitech.webutils.SearchExcelDataReport;
import com.yukthitech.webutils.annotations.ActionName;
import com.yukthitech.webutils.common.FileInfo;
import com.yukthitech.webutils.common.IWebUtilsActionConstants;
import com.yukthitech.webutils.common.IWebUtilsCommonConstants;
import com.yukthitech.webutils.common.SearchExecutionModel;
import com.yukthitech.webutils.common.client.IRequestCustomizer;
import com.yukthitech.webutils.common.controllers.IExtensionController;
import com.yukthitech.webutils.common.models.ModelDefResponse;
import com.yukthitech.webutils.common.models.def.ModelDef;
import com.yukthitech.webutils.common.search.ExecuteSearchResponse;
import com.yukthitech.webutils.common.search.ISearchController;
import com.yukthitech.webutils.common.search.SearchResponse;
import com.yukthitech.webutils.controllers.BaseController;
import com.yukthitech.webutils.services.ValidationService;
import com.yukthitech.webutils.utils.WebAttachmentUtils;

/**
 * Controller for fetching LOV values.
 * @author akiran
 */
@RestController
@ActionName(ACTION_PREFIX_SEARCH)
@RequestMapping("/search")
public class SearchController extends BaseController implements ISearchController
{
	private static Logger logger = LogManager.getLogger(SearchController.class);
	
	@Autowired
	private SearchService searchService;

	@Autowired
	private ValidationService validationService;
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	@Autowired
	private HttpServletResponse response;
	
	/**
	 * Used to export search results in excel format
	 */
	private ExcelExporter excelExporter = new ExcelExporter();
	
	//default block
	{
		objectMapper.setDateFormat(IWebUtilsCommonConstants.DEFAULT_DATE_FORMAT);
	}
	
	@Override
	@ActionName(ACTION_TYPE_FETCH_QUERY_DEF)
	@ResponseBody
	@RequestMapping(value = "/fetch/{" + PARAM_NAME + "}/query/def", method = RequestMethod.GET)
	public ModelDefResponse fetchSearchQueryDef(@PathVariable(PARAM_NAME) String queryName)
	{
		logger.trace("fetchSearchQueryDef is called for query - {}", queryName);
		
		return new ModelDefResponse( searchService.getSearhQueryDefinition(queryName) );
	}

	@Override
	@ActionName(ACTION_TYPE_FETCH_RESULT_DEF)
	@ResponseBody
	@RequestMapping(value = "/fetch/{" + PARAM_NAME + "}/execute", method = RequestMethod.GET)
	public ModelDefResponse fetchSearchResultDef(@PathVariable(PARAM_NAME) String queryName)
	{
		logger.trace("fetchSearchResultDef is called for query - {}", queryName);
		
		return new ModelDefResponse( searchService.getSearhResultDefinition(queryName) );
	}
	
	@Override
	@ActionName(ACTION_TYPE_EXECUTE)
	@ResponseBody
	@RequestMapping(value = "/execute/{" + PARAM_NAME + "}", method = RequestMethod.GET)
	public ExecuteSearchResponse executeSearch(@PathVariable(PARAM_NAME) String queryName, @Valid SearchExecutionModel searchExecutionModel) throws Exception
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
		
		validationService.validate(query);
		
		return searchService.executeSearch(queryName, query, searchExecutionModel);
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.webutils.common.search.ISearchController#executeSearchObjects(java.lang.String, com.yukthitech.webutils.common.SearchExecutionModel)
	 */
	@Override
	@ActionName(IWebUtilsActionConstants.ACTION_TYPE_SEARCH)
	@ResponseBody
	@RequestMapping(value = "/search/{" + PARAM_NAME + "}", method = RequestMethod.GET)
	public SearchResponse executeSearchObjects(@PathVariable(PARAM_NAME) String queryName, @Valid SearchExecutionModel searchExecutionModel) throws Exception
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
		
		validationService.validate(query);
		
		List<Object> results = searchService.searchObjects(queryName, query, searchExecutionModel);
		
		return new SearchResponse(results); 
	}

	@Override
	@ActionName(ACTION_TYPE_EXPORT)
	@ResponseBody
	@RequestMapping(value = "/export/{" + PARAM_NAME + "}", method = RequestMethod.GET)
	public void exportSearch(@PathVariable(PARAM_NAME) String queryName, @Valid SearchExecutionModel searchExecutionModel) throws Exception
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
		
		validationService.validate(query);
		
		ModelDef searchResultDef = searchService.getSearhResultDefinition(queryName);
		ExecuteSearchResponse results = searchService.executeSearch(queryName, query, new SearchExecutionModel(1, false, true));

		SearchExcelDataReport searchExcelDataReport = new SearchExcelDataReport("Results", results);
		File tempFile = File.createTempFile(queryName, ".xls");
		
		excelExporter.generateExcelSheet(tempFile.getPath(), searchExcelDataReport);
		
		WebAttachmentUtils.sendFile(response, new FileInfo(searchResultDef.getLabel() + ".xls", tempFile, WebAttachmentUtils.MIME_MS_EXCEL_FILE), true, true);
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.webutils.common.controllers.IClientController#setRequestCustomizer(com.yukthitech.webutils.common.client.IRequestCustomizer)
	 */
	@Override
	public ISearchController setRequestCustomizer(IRequestCustomizer customizer)
	{
		return null;
	}
}
