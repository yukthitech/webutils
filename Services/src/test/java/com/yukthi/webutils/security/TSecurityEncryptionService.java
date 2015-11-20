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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthi.utils.CommonUtils;
import com.yukthi.utils.ReflectionUtils;
import com.yukthi.webutils.WebutilsConfiguration;

/**
 * @author akiran
 *
 */
public class TSecurityEncryptionService
{
	private static Logger logger = LogManager.getLogger(TSecurityEncryptionService.class);
	
	/**
	 * Tests security token encryption/decryption when security fields are involved
	 * @throws Exception
	 */
	@Test
	public void testEncryptDecryptWithExtraFields() throws Exception
	{
		//set the test service
		WebutilsConfiguration config = new WebutilsConfiguration();
		config.setSecretKey("#AS#$%^Fe135EF@4");
		config.setUserDetailsType(UserDetails2.class);
		
		SecurityEncryptionService securityEncryptionService = new SecurityEncryptionService();
		ReflectionUtils.setFieldValue(securityEncryptionService, "configuration", config);
		
		//create test user details
		UserDetails2 userDetails = new UserDetails2();
		userDetails.setUserId(1234L);
		userDetails.setRoles(CommonUtils.toSet(TestRole.EMP_ADMIN, TestRole.ADMIN));
		userDetails.setSessionStartTime(10L);
		
		String encryptedVal = securityEncryptionService.encrypt(userDetails);
		logger.debug("Got encrypted string as - " + encryptedVal);
		
		
		//decrypt and validate
		UserDetails2 decryptedUserDetails = (UserDetails2)securityEncryptionService.decrypt(encryptedVal);
		
		Assert.assertNotNull(decryptedUserDetails);
		Assert.assertEquals(decryptedUserDetails.getUserId(), userDetails.getUserId());
		Assert.assertEquals(decryptedUserDetails.getRoles(), userDetails.getRoles());
		Assert.assertEquals(decryptedUserDetails.getField1(), userDetails.getField1());
		Assert.assertEquals(decryptedUserDetails.getField2(), userDetails.getField2());
		Assert.assertEquals(decryptedUserDetails.getSessionStartTime(), userDetails.getSessionStartTime());
	}

	/**
	 * Tests security token encryption/decryption when security fields are not involved
	 * @throws Exception
	 */
	@Test
	public void testEncryptDecryptWithoutExtraFields() throws Exception
	{
		//set the test service
		WebutilsConfiguration config = new WebutilsConfiguration();
		config.setSecretKey("#AS#$%^Fe135EF@4");
		config.setUserDetailsType(UserDetails1.class);
		
		SecurityEncryptionService securityEncryptionService = new SecurityEncryptionService();
		ReflectionUtils.setFieldValue(securityEncryptionService, "configuration", config);
		
		//create test user details
		UserDetails1 userDetails = new UserDetails1();
		userDetails.setUserId(1234L);
		
		String encryptedVal = securityEncryptionService.encrypt(userDetails);
		logger.debug("Got encrypted string as - " + encryptedVal);
		
		
		//decrypt and validate
		UserDetails1 decryptedUserDetails = (UserDetails1)securityEncryptionService.decrypt(encryptedVal);
		
		Assert.assertNotNull(decryptedUserDetails);
		Assert.assertEquals(decryptedUserDetails.getUserId(), userDetails.getUserId());
	}
}
