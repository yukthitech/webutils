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

package com.test.yukthitech.webutils.client;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.test.yukthitech.webutils.models.TestEmpSearchQuery;
import com.test.yukthitech.webutils.models.TestEmpSearchResult;
import com.test.yukthitech.webutils.models.TestEmployeeModel;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.utils.rest.RestClient;
import com.yukthitech.utils.rest.RestRequest;
import com.yukthitech.utils.rest.RestResult;
import com.yukthitech.webutils.client.ActionRequestBuilder;
import com.yukthitech.webutils.client.RestException;
import com.yukthitech.webutils.client.helpers.SearchHelper;
import com.yukthitech.webutils.common.IWebUtilsCommonConstants;
import com.yukthitech.webutils.common.models.BaseResponse;
import com.yukthitech.webutils.common.models.BasicSaveResponse;
import com.yukthitech.webutils.common.models.def.FieldDef;
import com.yukthitech.webutils.common.models.def.ModelDef;
import com.yukthitech.webutils.common.search.ExecuteSearchResponse;
import com.yukthitech.webutils.common.search.ISearchController;
import com.yukthitech.webutils.common.search.SearchRow;

/**
 * Test LOV value fetching from server
 * @author akiran
 */
public class TFSearchQuery extends TFBase
{
	private ISearchController searchController;
	
	private SearchHelper searchHelper = new SearchHelper();
	
	private long addEmployee(String name, long salary)
	{
		TestEmployeeModel emp = new TestEmployeeModel(name, salary);
		
		RestRequest<?> request = ActionRequestBuilder.buildRequest(
				clientContext, 
				"employee.save", emp, null);
		
		RestClient client = clientContext.getRestClient();
		
		RestResult<BasicSaveResponse> result = client.invokeJsonRequest(request, BasicSaveResponse.class);
		BasicSaveResponse response = result.getValue();
		
		if(response == null || response.getCode() != IWebUtilsCommonConstants.RESPONSE_CODE_SUCCESS)
		{
			if(response != null)
			{
				throw new RestException(response.getMessage(), response.getCode());
			}
			
			throw new InvalidStateException("Unknow error occurred - {}", result);
		}
		
		Assert.assertEquals(response.getCode(), IWebUtilsCommonConstants.RESPONSE_CODE_SUCCESS);
	
		return response.getId();
	}
	
	@BeforeClass
	private void setup()
	{
		searchController = super.clientControllerFactory.getController(ISearchController.class);
		
		addEmployee("abc", 100);
		addEmployee("hbc", 200);
		addEmployee("tab", 300);
		addEmployee("cab", 400);
		addEmployee("pop", 500);
		addEmployee("pip", 600);
		addEmployee("tap", 700);
		addEmployee("sap", 710);
		addEmployee("kap", 830);
		addEmployee("rap", 710);
		addEmployee("lap", 7230);
		addEmployee("dap", 710);
	}

	@Test
	public void testSearchQueryModel()
	{
		ModelDef modelDef = searchController.fetchSearchQueryDef("empSearch").getModelDef();
		
		Assert.assertEquals(modelDef.getName(), TestEmpSearchQuery.class.getSimpleName());
		Assert.assertEquals(modelDef.getFields().size(), 2);
		
		Set<String> fieldNames = modelDef.getFields().stream()
				.map(field -> field.getName())
				.collect(Collectors.toSet());
		Assert.assertEquals(fieldNames, CommonUtils.toSet("id", "name"));
	}

	@Test
	public void testSearchQueryResult()
	{
		ModelDef modelDef = searchController.fetchSearchResultDef("empSearch").getModelDef();
		
		Assert.assertEquals(modelDef.getName(), TestEmpSearchResult.class.getSimpleName());
		Assert.assertEquals(modelDef.getFields().size(), 3);
		
		Map<String, FieldDef> map = CommonUtils.buildMap(modelDef.getFields(), "name", null);
		Assert.assertEquals(map.keySet(), CommonUtils.toSet("id", "name", "salary"));
	}

	@Test
	public void testSearchResults()
	{
		TestEmpSearchQuery query = new TestEmpSearchQuery("%a%");
		ExecuteSearchResponse response = searchHelper.executeSearchQuery(clientContext, "empSearch", query, -1, -1);
		Assert.assertEquals(response.getSearchResults().size(), 9);
		
		response = searchHelper.executeSearchQuery(clientContext, "empSearch", query, 1, 3);
		Assert.assertEquals(response.getSearchResults().size(), 3);
		
		List<SearchRow> results = response.getSearchResults();
		
		//to ensure bean conversion is good, check first bean
		Assert.assertEquals(results.get(0).getData().get(1), "abc");
		Assert.assertEquals(results.get(0).getData().get(2), "100");
	}
	
	/**
	 * Tests execution of search query which is authorized
	 */
	@Test
	public void testSearchAuthorization()
	{
		TestEmpSearchQuery query = new TestEmpSearchQuery("%a%");
		ExecuteSearchResponse response = searchHelper.executeSearchQuery(clientContext, "empSearchAuthorized", query, -1, -1);
		
		Assert.assertNotNull(response);
	}
	
	/**
	 * Tests execution of search query which is unauthorized
	 */
	@Test
	public void testSearchUnauthorized()
	{
		TestEmpSearchQuery query = new TestEmpSearchQuery("%a%");
		
		try
		{
			searchHelper.executeSearchQuery(clientContext, "empSearchUnauthorized", query, -1, -1);
			Assert.fail("No exception is thrown when unauthorized search method is accessed");
		}catch(RestException ex)
		{
			Assert.assertEquals(ex.getResponseCode(), IWebUtilsCommonConstants.RESPONSE_CODE_AUTHORIZATION_ERROR);
		}
	}
	
	@Test
	public void testSearchWithViolations()
	{
		TestEmpSearchQuery query = new TestEmpSearchQuery(null);
		
		try
		{
			searchHelper.executeSearchQuery(clientContext, "empSearchAuthorized", query, -1, -1);
			Assert.fail("No exception is thrown when query is passing with mandatory fields as null");
		}catch(RestException ex)
		{
			Assert.assertEquals(ex.getResponseCode(), IWebUtilsCommonConstants.RESPONSE_CODE_INVALID_REQUEST);
		}
	}

	@AfterClass
	private void clean()
	{
		RestClient client = clientContext.getRestClient();
		RestRequest<?> request = ActionRequestBuilder.buildRequest(super.clientContext, "employee.deleteAll", null, null);
		client.invokeJsonRequest(request, BaseResponse.class);
	}
}
