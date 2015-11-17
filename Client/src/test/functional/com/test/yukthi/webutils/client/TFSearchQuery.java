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

package com.test.yukthi.webutils.client;

import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.test.yukthi.webutils.models.EmpSearchQuery;
import com.test.yukthi.webutils.models.EmpSearchResult;
import com.test.yukthi.webutils.models.EmployeeModel;
import com.yukthi.utils.CommonUtils;
import com.yukthi.utils.exceptions.InvalidStateException;
import com.yukthi.utils.rest.RestClient;
import com.yukthi.utils.rest.RestRequest;
import com.yukthi.utils.rest.RestResult;
import com.yukthi.webutils.client.ActionRequestBuilder;
import com.yukthi.webutils.client.RestException;
import com.yukthi.webutils.client.helpers.SearchHelper;
import com.yukthi.webutils.common.IWebUtilsCommonConstants;
import com.yukthi.webutils.common.models.BaseResponse;
import com.yukthi.webutils.common.models.BasicSaveResponse;
import com.yukthi.webutils.common.models.def.FieldDef;
import com.yukthi.webutils.common.models.def.ModelDef;

/**
 * Test LOV value fetching from server
 * @author akiran
 */
public class TFSearchQuery extends TFBase
{
	private SearchHelper searchHelper = new SearchHelper();
	
	private long addEmployee(String name, long salary)
	{
		EmployeeModel emp = new EmployeeModel(name, salary);
		
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
		ModelDef modelDef = searchHelper.getSearchQueryDef(clientContext, "empSearch");
		
		Assert.assertEquals(modelDef.getName(), EmpSearchQuery.class.getSimpleName());
		Assert.assertEquals(modelDef.getFields().size(), 1);
		Assert.assertEquals(modelDef.getFields().get(0).getName(), "name");
	}

	@Test
	public void testSearchQueryResult()
	{
		ModelDef modelDef = searchHelper.getSearchResultDef(clientContext, "empSearch");
		
		Assert.assertEquals(modelDef.getName(), EmpSearchResult.class.getSimpleName());
		Assert.assertEquals(modelDef.getFields().size(), 3);
		
		Map<String, FieldDef> map = CommonUtils.buildMap(modelDef.getFields(), "name", null);
		Assert.assertEquals(map.keySet(), CommonUtils.toSet("id", "name", "salary"));
	}

	@Test
	public void testSearchResults()
	{
		EmpSearchQuery query = new EmpSearchQuery("%a%");
		List<EmpSearchResult> results = searchHelper.executeSearchQuery(clientContext, "empSearch", query, -1, EmpSearchResult.class);
		Assert.assertEquals(results.size(), 9);
		
		results = searchHelper.executeSearchQuery(clientContext, "empSearch", query, 3, EmpSearchResult.class);
		Assert.assertEquals(results.size(), 3);
		
		//to ensure bean conversion is good, check first bean
		Assert.assertEquals(results.get(0).getName(), "abc");
		Assert.assertEquals(results.get(0).getSalary(), 100);
	}
	
	@AfterClass
	private void clean()
	{
		RestClient client = clientContext.getRestClient();
		RestRequest<?> request = ActionRequestBuilder.buildRequest(super.clientContext, "employee.deleteAll", null, null);
		client.invokeJsonRequest(request, BaseResponse.class);
	}
}
