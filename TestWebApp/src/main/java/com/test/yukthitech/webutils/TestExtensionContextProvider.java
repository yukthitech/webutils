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

package com.test.yukthitech.webutils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.test.yukthitech.webutils.entity.CustomerEntity;
import com.yukthitech.webutils.controllers.IExtensionContextProvider;
import com.yukthitech.webutils.extensions.ExtensionDetails;
import com.yukthitech.webutils.extensions.ExtensionEntityDetails;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author akiran
 *
 */
@Component
public class TestExtensionContextProvider implements IExtensionContextProvider
{
	@Autowired
	private HttpServletRequest request;

	@Override
	public ExtensionDetails getExtensionDetails(String extensionName, ExtensionEntityDetails extensionPointDetails)
	{
		String customerId = request.getHeader("customerId");
		
		if(customerId == null)
		{
			return new ExtensionDetails();
		}
		
		return new ExtensionDetails(CustomerEntity.class, Long.parseLong(customerId));
	}
}
