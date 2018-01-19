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

import com.yukthitech.webutils.common.models.BaseResponse;

/**
 * Exception to be thrown when an error occurs while invoking apis
 * @author akiran
 */
public class RestException extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Http status code during REST error.
	 */
	private int statusCode;
	
	/**
	 * Response code from server.
	 */
	private int responseCode;
	
	/**
	 * Response error message from server.
	 */
	private String responseMessage;

	/**
	 * Instantiates a new rest exception.
	 *
	 * @param message message
	 * @param statusCode the http status code
	 * @param response the response object
	 */
	public RestException(String message, int statusCode, BaseResponse response)
	{
		super(buildMessage(message, statusCode, response));
		
		this.statusCode = statusCode;
		this.responseCode = (response != null)? response.getCode() : 0;
		this.responseMessage = (response != null)? response.getMessage() : null;
	}
	
	/**
	 * Builds exception message on the parameters specified.
	 * @param message
	 * @param statusCode
	 * @param responseCode
	 * @param responseMessage
	 * @return
	 */
	private static String buildMessage(String message, int statusCode, BaseResponse response)
	{
		StringBuilder builder = new StringBuilder(message);
		
		builder.append(" [Http Status: ").append(statusCode);
		
		if(response != null)
		{
			builder.append(", Response Code: ").append(response.getCode()).append(", Response Message: ").append(response.getMessage());
		}
		
		builder.append("]");
		
		return builder.toString();
	}
	
	/**
	 * Instantiates a new rest exception.
	 * @param message Message for exception
	 * @param statusCode the status code
	 */
	public RestException(String message, int statusCode)
	{
		this(message, statusCode, null);
	}

	/**
	 * Gets the http status code during REST error.
	 *
	 * @return the http status code during REST error
	 */
	public int getStatusCode()
	{
		return statusCode;
	}

	/**
	 * Gets the response code from server.
	 *
	 * @return the response code from server
	 */
	public int getResponseCode()
	{
		return responseCode;
	}

	/**
	 * Gets the response error message from server.
	 *
	 * @return the response error message from server
	 */
	public String getResponseMessage()
	{
		return responseMessage;
	}
}
