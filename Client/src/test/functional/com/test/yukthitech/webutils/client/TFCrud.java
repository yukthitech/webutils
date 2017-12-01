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

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.test.yukthitech.webutils.models.IEmployeeController;
import com.test.yukthitech.webutils.models.TestEmployeeModel;
import com.yukthitech.webutils.common.IWebUtilsCommonConstants;
import com.yukthitech.webutils.common.models.BasicReadResponse;
import com.yukthitech.webutils.common.models.BasicSaveResponse;

/**
 * @author akiran
 *
 */
public class TFCrud extends TFBase
{
	private IEmployeeController employeeController;
	
	@BeforeClass
	public void setup()
	{
		employeeController = super.clientControllerFactory.getController(IEmployeeController.class);
	}
	
	private TestEmployeeModel getEmployee(long empId)
	{
		BasicReadResponse<TestEmployeeModel> response = employeeController.fetch(empId);
		return response.getModel();
	}
	
	@Test
	public void testSave()
	{
		TestEmployeeModel emp = new TestEmployeeModel("TestEmp", 1000);
		BasicSaveResponse response = employeeController.save(emp);
		
		Assert.assertEquals(response.getCode(), IWebUtilsCommonConstants.RESPONSE_CODE_SUCCESS);

		//validate save
		TestEmployeeModel savedEmp = getEmployee(response.getId());
		Assert.assertEquals(savedEmp.getName(), "TestEmp");
		Assert.assertEquals(savedEmp.getSalary(), 1000L);
	}

	@AfterClass
	private void clean()
	{
		employeeController.deleteAll();
	}
}
