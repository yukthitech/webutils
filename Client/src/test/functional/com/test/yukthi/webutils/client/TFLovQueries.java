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
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.test.yukthi.webutils.models.TestEmployeeModel;
import com.yukthi.utils.CommonUtils;
import com.yukthi.utils.exceptions.InvalidStateException;
import com.yukthi.utils.rest.RestClient;
import com.yukthi.utils.rest.RestRequest;
import com.yukthi.utils.rest.RestResult;
import com.yukthi.webutils.client.ActionRequestBuilder;
import com.yukthi.webutils.client.RestException;
import com.yukthi.webutils.client.helpers.LovHelper;
import com.yukthi.webutils.common.IWebUtilsCommonConstants;
import com.yukthi.webutils.common.LovType;
import com.yukthi.webutils.common.models.BaseResponse;
import com.yukthi.webutils.common.models.BasicSaveResponse;
import com.yukthi.webutils.common.models.ValueLabel;

/**
 * Test LOV value fetching from server
 * @author akiran
 */
public class TFLovQueries extends TFBase
{
	private static Logger logger = LogManager.getLogger(TFLovQueries.class);
	
	private LovHelper lovHelper = new LovHelper();
	
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
		List<ValueLabel> lovList = lovHelper.getStaticLov(super.clientContext, LovType.class.getName());
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
		List<ValueLabel> lovList = lovHelper.getDynamicLov(super.clientContext, "employeeLov");
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
		List<ValueLabel> lovList = lovHelper.getDynamicLov(super.clientContext, "employeeLovAuthorized");
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
			lovHelper.getDynamicLov(super.clientContext, "employeeLovUnauthorized");
			Assert.fail("No exception is thrown when unauthorized lov method is accessed");
		}catch(RestException ex)
		{
			Assert.assertEquals(ex.getResponseCode(), IWebUtilsCommonConstants.RESPONSE_CODE_AUTHORIZATION_ERROR);
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
