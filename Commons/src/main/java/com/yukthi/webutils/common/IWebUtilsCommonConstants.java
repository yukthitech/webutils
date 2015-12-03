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

package com.yukthi.webutils.common;

import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

/**
 * Different response codes
 * @author akiran
 */
public interface IWebUtilsCommonConstants
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

	/**
	 * Response code to be used when authentication fails
	 */
	public int RESPONSE_CODE_AUTHENTICATION_ERROR = 4401;

	/**
	 * Response code to be used when authorization fails
	 */
	public int RESPONSE_CODE_AUTHORIZATION_ERROR = 4402;

	/**
	 * Response code to be used when session is timed out or expired
	 */
	public int RESPONSE_CODE_SESSION_TIMEOUT_ERROR = 4403;

	/**
	 * Request/response header that will hold authorization token
	 */
	public String HEADER_AUTHORIZATION_TOKEN = "AUTH_TOKEN";
	
	
	public String AUTH_GROUP_URI = "/auth";
	public String LOGIN_URI_PATH = "/login";
	
	public String ACTION_GROUP_URI = "/actions";
	public String FETCH_URI_PATH = "/fetch";
	
	/**
	 * URI for app login
	 */
	public String LOGIN_URI = AUTH_GROUP_URI + LOGIN_URI_PATH;
	
	/**
	 * URI to fetch actions
	 */
	public String ACTIONS_FETCH_URI = ACTION_GROUP_URI + FETCH_URI_PATH;

	/**
	 * Pattern to validate numeric string values
	 */
	public Pattern INT_PATTERN = Pattern.compile("\\d+");
	
	/**
	 * Pattern to validate decimal string values
	 */
	public Pattern DECIMAL_PATTERN = Pattern.compile("\\d+\\.\\d+");
	
	/**
	 * Default date format to be used
	 */
	public SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
}
