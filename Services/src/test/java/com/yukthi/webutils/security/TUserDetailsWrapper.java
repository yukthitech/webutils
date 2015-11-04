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

package com.yukthi.webutils.security;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author akiran
 *
 */
public class TUserDetailsWrapper
{
	/**
	 * Test wrapper when security fields are involved
	 */
	@Test
	public void testWithSecFields()
	{
		UserDetailsWrapper wrapper = new UserDetailsWrapper(UserDetails2.class);
		
		//create details and wrapper instance
		UserDetails2 det = new UserDetails2(103, 10456);
		
		//ensure fetching is proper
		String values[] = wrapper.getSecurityFields(det);
		Assert.assertEquals(values.length, 2);
		Assert.assertEquals(values[0], "103");
		Assert.assertEquals(values[1], "10456");
		
		
		//ensure setting field values is proper
		UserDetails2 newInst = (UserDetails2)wrapper.newDetails();
		wrapper.setSecurityFields(newInst, values);
		
		Assert.assertEquals(newInst.getField1(), 103);
		Assert.assertEquals(newInst.getField2(), 10456L);
	}

	/**
	 * Test wrapper when security fields are not involved
	 */
	@Test
	public void testWithoutSecFields()
	{
		UserDetailsWrapper wrapper = new UserDetailsWrapper(UserDetails1.class);
		
		//create details and wrapper instance
		UserDetails1 det = new UserDetails1();
		
		//ensure fetching is proper
		String values[] = wrapper.getSecurityFields(det);
		Assert.assertEquals(values.length, 0);
		
		
		//ensure setting field values is proper
		UserDetails1 newInst = (UserDetails1)wrapper.newDetails();
		wrapper.setSecurityFields(newInst, values);
	}
}
