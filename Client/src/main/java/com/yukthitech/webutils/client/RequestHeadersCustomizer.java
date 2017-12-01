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

package com.yukthitech.webutils.client;

import java.util.Map;

import com.yukthitech.utils.rest.RestRequest;
import com.yukthitech.webutils.common.client.IRequestCustomizer;

/**
 * Request customizer ( {@link IRequestCustomizer} ) that can be used to set headers on the next immediate
 * rest request going to be made.
 * 
 * @author akiran
 */
public class RequestHeadersCustomizer implements IRequestCustomizer
{
	/**
	 * Headers to set on request.
	 */
	private Map<String, Object> headers;
	
	/**
	 * Instantiates a new request headers customizer.
	 *
	 * @param headers the headers
	 */
	public RequestHeadersCustomizer(Map<String, Object> headers)
	{
		this.headers = headers;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.webutils.common.client.IRequestCustomizer#customize(com.yukthitech.utils.rest.RestRequest)
	 */
	@Override
	public void customize(RestRequest<?> request)
	{
		//if no headers are specified, ignore customization
		if(headers == null)
		{
			return;
		}
		
		//loop through headers and add them to request
		for(String name : headers.keySet())
		{
			request.addHeader(name, "" + headers.get(name));
		}
	}
}
