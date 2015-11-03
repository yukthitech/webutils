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

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthi.utils.CommonUtils;

/**
 * @author akiran
 *
 */
public class TSecurityInterceptor
{
	private static Logger logger = LogManager.getLogger(TSecurityInterceptor.class);
	private SecurityInterceptor securityInterceptor = new SecurityInterceptor(TestRole.class, "#AS#$%^Fe135EF@4");
	
	@Test
	public void testEncryptDecrypt()
	{
		long userId = 1234L;
		Object roles[] = new Object[]{TestRole.EMP_ADMIN, TestRole.ADMIN};
		
		String encryptedVal = securityInterceptor.encrypt(1234, roles);
		logger.debug("Got encryptes string as - " + encryptedVal);
		
		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		UserDetails<?> userDetails = securityInterceptor.decrypt(encryptedVal, response);
		
		Assert.assertNotNull(userDetails);
		Assert.assertEquals(userDetails.getUserId(), userId);
		Assert.assertEquals( CommonUtils.toSet(userDetails.getRoles()) , CommonUtils.toSet(roles) );
	}
}
