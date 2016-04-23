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

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.test.yukthi.webutils.models.ITestController;
import com.test.yukthi.webutils.models.TestBean;
import com.yukthi.webutils.client.RestException;
import com.yukthi.webutils.common.IWebUtilsCommonConstants;
import com.yukthi.webutils.common.models.BaseResponse;

/**
 * Test LOV value fetching from server
 * @author akiran
 */
public class TFAuthorization extends TFBase
{
	private ITestController testController;
	
	@BeforeClass
	public void setup()
	{
		testController = super.clientControllerFactory.getController(ITestController.class);
	}
	
	/**
	 * Tests when roles are not sufficient.
	 */
	@Test
	public void testUnauthorizedAction()
	{
		try
		{
			testController.secured1(new TestBean("name", 25, "test", "test"));
			Assert.fail("No rest exception is thrown");
		}catch(RestException ex)
		{
			Assert.assertEquals(ex.getResponseCode(), IWebUtilsCommonConstants.RESPONSE_CODE_AUTHORIZATION_ERROR);
		}
	}
	
	/**
	 * Tests when roles are sufficient.
	 */
	@Test
	public void testAuthorizedAction()
	{
		BaseResponse response = testController.secured2(new TestBean("name", 25, "test", "test"));
		
		Assert.assertEquals(response.getCode(), IWebUtilsCommonConstants.RESPONSE_CODE_SUCCESS);
	}
}
