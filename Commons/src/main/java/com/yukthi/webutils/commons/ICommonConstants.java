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

package com.yukthi.webutils.commons;

/**
 * Different response codes
 * @author akiran
 */
public interface ICommonConstants
{
	/**
	 * Success code to be used for successful request processing
	 */
	public int RESPONSE_CODE_SUCCESS = 0;
	
	/**
	 * Response code to be used when input request validation failed
	 */
	public int RESPONSE_CODE_INVALID_REQUEST = 4400;
	
	/**
	 * Response code to be used when unhandled error occurs on server
	 */
	public int RESPONSE_CODE_UNHANDLED_SERVER_ERROR = 4500;
}
