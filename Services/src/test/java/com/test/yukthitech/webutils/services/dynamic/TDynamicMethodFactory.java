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

package com.test.yukthitech.webutils.services.dynamic;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.yukthitech.utils.ReflectionUtils;
import com.yukthitech.webutils.WebutilsContext;
import com.yukthitech.webutils.common.lov.LovType;
import com.yukthitech.webutils.services.dynamic.DynamicMethod;
import com.yukthitech.webutils.services.dynamic.DynamicMethodFactory;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Test cases to test dynamic method functionality
 * @author akiran
 */
public class TDynamicMethodFactory
{
	
	@BeforeClass
	private void setup() throws Exception
	{
	}
	
	/**
	 * Tests dynamic method invocation with proper values
	 * @throws Exception
	 */
	@Test
	public void testWithProperValues() throws Exception
	{
		DynamicMethodFactory factory = new DynamicMethodFactory();
		DynamicMethod dynamicMethod1 = factory.buildDynamicMethod(DynamicMethodHolder.class, 
					DynamicMethodHolder.class.getMethod("dynMethod1", new Class[]{int.class, LovType.class}) );
		dynamicMethod1.setDefaultObject(new DynamicMethodHolder());
		
		WebutilsContext context = WebutilsContext.getContext();
		HttpServletRequest request = mock(HttpServletRequest.class);
		
		ReflectionUtils.setFieldValue(dynamicMethod1, "request", request);

		//set test values
		when(request.getParameter("param1")).thenReturn("10");
		context.addAttribute("lovType", LovType.DYNAMIC_TYPE.toString());
		
		Assert.assertEquals(dynamicMethod1.invoke(), "10-" + LovType.DYNAMIC_TYPE);
	}

	/**
	 * Tests dynamic method invocation with null values
	 * @throws Exception
	 */
	@Test
	public void testWithNullValues() throws Exception
	{
		DynamicMethodFactory factory = new DynamicMethodFactory();
		DynamicMethod dynamicMethod1 = factory.buildDynamicMethod(DynamicMethodHolder.class, 
					DynamicMethodHolder.class.getMethod("dynMethod1", new Class[]{int.class, LovType.class}) );
		dynamicMethod1.setDefaultObject(new DynamicMethodHolder());
		
		WebutilsContext context = WebutilsContext.getContext();
		HttpServletRequest request = mock(HttpServletRequest.class);
		
		ReflectionUtils.setFieldValue(dynamicMethod1, "request", request);

		//set test data
		when(request.getParameter("param1")).thenReturn(null);
		context.addAttribute("lovType", null);
		
		Assert.assertEquals(dynamicMethod1.invoke(), "0-null");
	}

	/**
	 * Test for dynamic method with no arguments
	 * @throws Exception
	 */
	@Test
	public void testWithNoArgMethod() throws Exception
	{
		DynamicMethodFactory factory = new DynamicMethodFactory();
		DynamicMethod dynamicMethod2 = factory.buildDynamicMethod(DynamicMethodHolder.class, 
				DynamicMethodHolder.class.getMethod("dynMethod2", new Class[]{}) );
		dynamicMethod2.setDefaultObject(new DynamicMethodHolder());
		
		Assert.assertEquals(dynamicMethod2.invoke(), "success");
	}
}
