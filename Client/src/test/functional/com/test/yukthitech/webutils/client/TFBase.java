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

import java.util.Properties;

import org.testng.annotations.BeforeClass;

import com.yukthitech.webutils.client.ClientContext;
import com.yukthitech.webutils.client.ClientControllerFactory;

/**
 * Base class for test cases
 * @author akiran
 */
public abstract class TFBase
{
	protected String baseUrl;
	protected ClientContext clientContext;
	protected ClientControllerFactory clientControllerFactory;
	
	protected Properties configuration;
	
	@BeforeClass
	public final void init() throws Exception
	{
		configuration = new Properties();
		configuration.load(TFBase.class.getResourceAsStream("/test-env.properties"));
		
		String baseUrl = configuration.getProperty("test.base.url");
		
		clientContext = new ClientContext(baseUrl);
		this.baseUrl = baseUrl;
		
		clientContext.authenticate(configuration.getProperty("test.username"), configuration.getProperty("test.password"));
		
		clientControllerFactory = new ClientControllerFactory(clientContext);
	}
	
}
