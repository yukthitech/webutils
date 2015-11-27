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

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.test.yukthi.webutils.models.EmployeeModel;
import com.yukthi.utils.CommonUtils;
import com.yukthi.utils.exceptions.InvalidStateException;
import com.yukthi.utils.rest.RestClient;
import com.yukthi.utils.rest.RestRequest;
import com.yukthi.utils.rest.RestResult;
import com.yukthi.webutils.client.ActionRequestBuilder;
import com.yukthi.webutils.client.RestException;
import com.yukthi.webutils.common.IWebUtilsCommonConstants;
import com.yukthi.webutils.common.models.BaseResponse;
import com.yukthi.webutils.common.models.BasicSaveResponse;

/**
 * @author akiran
 *
 */
public class TFCrud extends TFBase
{
	private EmployeeModel getEmployee(long empId)
	{
		RestRequest<?> request = ActionRequestBuilder.buildRequest(
				clientContext, 
				"employee.fetch", null, CommonUtils.toMap(
						"id", "" + empId
					));
		
		RestClient client = clientContext.getRestClient();
		
		RestResult<EmployeeModel> result = client.invokeJsonRequest(request, EmployeeModel.class);
		return result.getValue();
	}
	
	@Test
	public void testSave()
	{
		EmployeeModel emp = new EmployeeModel("TestEmp", 1000);
		
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

		//validate save
		EmployeeModel savedEmp = getEmployee(response.getId());
		Assert.assertEquals(savedEmp.getName(), "TestEmp");
		Assert.assertEquals(savedEmp.getSalary(), 1000L);
		Assert.assertTrue( DateUtils.isSameDay(savedEmp.getCreatedOn(), new Date()));
		Assert.assertTrue( DateUtils.isSameDay(savedEmp.getUpdatedOn(), new Date()));
	}

	@AfterClass
	private void clean()
	{
		RestClient client = clientContext.getRestClient();
		RestRequest<?> request = ActionRequestBuilder.buildRequest(super.clientContext, "employee.deleteAll", null, null);
		client.invokeJsonRequest(request, BaseResponse.class);
	}
}
