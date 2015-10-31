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
import org.testng.annotations.Test;

import com.yukthi.utils.CommonUtils;
import com.yukthi.webutils.client.helpers.LovHelper;
import com.yukthi.webutils.commons.LovType;
import com.yukthi.webutils.models.ValueLabel;

/**
 * Test LOV value fetching from server
 * @author akiran
 */
public class TFLovQueries extends TFBase
{
	private static Logger logger = LogManager.getLogger(TFLovQueries.class);
	
	private LovHelper lovHelper = new LovHelper();
	
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
		
		Assert.assertEquals(lovList.size(), 3);
		
		//ensure the labels are same test data
		Set<String> names = CommonUtils.toSet("Test1", "Test2", "Test3");
		
		for(ValueLabel vl : lovList)
		{
			Assert.assertTrue(names.remove(vl.getLabel()));
			Assert.assertTrue(Long.parseLong(vl.getValue()) > 0);
		}
		
		Assert.assertTrue(names.isEmpty());
	}
}
