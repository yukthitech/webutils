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
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.test.yukthitech.webutils.models.TestEmployeeModel;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.utils.rest.RestClient;
import com.yukthitech.utils.rest.RestRequest;
import com.yukthitech.utils.rest.RestResult;
import com.yukthitech.webutils.client.ActionRequestBuilder;
import com.yukthitech.webutils.client.RestException;
import com.yukthitech.webutils.common.IWebUtilsCommonConstants;
import com.yukthitech.webutils.common.LovType;
import com.yukthitech.webutils.common.controllers.ILovController;
import com.yukthitech.webutils.common.models.BaseResponse;
import com.yukthitech.webutils.common.models.BasicSaveResponse;
import com.yukthitech.webutils.common.models.ValueLabel;

/**
 * Test LOV value fetching from server
 * @author akiran
 */
public class TFLovQueries extends TFBase
{
	private static Logger logger = LogManager.getLogger(TFLovQueries.class);
	
	private ILovController lovController;
	
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
		lovController = super.clientControllerFactory.getController(ILovController.class);
		
		addEmployee("abc", 100);
		addEmployee("xyz", 200);
		addEmployee("efg", 300);
		addEmployee("zyx", 300);
	}

	
	/**
	 * Tests static LOV fetch work properly
	 */
	@Test
	public void testStaticLov()
	{
		List<ValueLabel> lovList = lovController.fetchLov(LovType.class.getName(), LovType.STATIC_TYPE).getLovList();
		logger.debug("Got LOV as - " + lovList);
		
		Assert.assertEquals(lovList.size(), LovType.values().length);
		
		for(ValueLabel vl : lovList)
		{
			Assert.assertNotNull(LovType.valueOf(vl.getValue()));
		}
	}

	/**
	 * Tests dynamic LOV fetch funcionality
	 */
	@Test
	public void testDynamicLov()
	{
		//get test lov dynamic values
		List<ValueLabel> lovList = lovController.fetchLov("employeeLov", LovType.DYNAMIC_TYPE).getLovList();
		logger.debug("Got LOV as - " + lovList);
		
		Assert.assertEquals(lovList.size(), 4);
		
		//ensure the labels are same test data
		Set<String> names = CommonUtils.toSet("abc", "efg", "xyz", "zyx");
		
		for(ValueLabel vl : lovList)
		{
			Assert.assertTrue(names.remove(vl.getLabel()));
			Assert.assertTrue(Long.parseLong(vl.getValue()) > 0);
		}
		
		Assert.assertTrue(names.isEmpty());
	}

	/**
	 * Tests execution of lov query which is authorized
	 */
	@Test
	public void testLovAuthorization()
	{
		List<ValueLabel> lovList = lovController.fetchLov("employeeLovAuthorized", LovType.DYNAMIC_TYPE).getLovList();
		logger.debug("Got LOV as - " + lovList);
		
		Assert.assertEquals(lovList.size(), 4);
	}
	
	/**
	 * Tests execution of lov query which is unauthorized
	 */
	@Test
	public void testLovUnauthorized()
	{
		try
		{
			lovController.fetchLov("employeeLovUnauthorized", LovType.DYNAMIC_TYPE).getLovList();
			Assert.fail("No exception is thrown when unauthorized lov method is accessed");
		}catch(RestException ex)
		{
			Assert.assertEquals(ex.getResponseCode(), IWebUtilsCommonConstants.RESPONSE_CODE_AUTHORIZATION_ERROR);
		}
	}
	
	@Test
	public void testDependencyLov()
	{
		List<ValueLabel> states = lovController.fetchLov("statesLov", LovType.DYNAMIC_TYPE).getLovList();
		
		Assert.assertEquals(states.size(), 2);
		
		String andhraId = states.get(0).getValue(), karnatakaId = states.get(1).getValue();

		List<ValueLabel> cities = lovController.fetchDependentLov("cityLov", andhraId).getLovList();
		Assert.assertEquals(cities.size(), 2);
		Assert.assertEquals(cities.get(0).getLabel(), "Hyderabad");
		Assert.assertEquals(cities.get(1).getLabel(), "Vijayawada");
		
		cities = lovController.fetchDependentLov("cityLov", karnatakaId).getLovList();
		Assert.assertEquals(cities.size(), 1);
		Assert.assertEquals(cities.get(0).getLabel(), "Bangalore");
	}

	@AfterClass
	private void clean()
	{
		RestClient client = clientContext.getRestClient();
		RestRequest<?> request = ActionRequestBuilder.buildRequest(super.clientContext, "employee.deleteAll", null, null);
		client.invokeJsonRequest(request, BaseResponse.class);
	}
}
