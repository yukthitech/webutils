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

package com.yukthi.webutils.common.models;

import com.yukthi.webutils.common.ICommonConstants;

/**
 * Base for all response classes to include response code and message
 */
public class BaseResponse
{
	/** 
	 * Response code. 
	 */
	private int code;

	/** 
	 * Response message. 
	 */
	private String message;

	/**
	 * Instantiates a new base response.
	 */
	public BaseResponse()
	{
		this(ICommonConstants.RESPONSE_CODE_SUCCESS, null);
	}

	/**
	 * Instantiates a new base response.
	 *
	 * @param code
	 *            the code
	 * @param message
	 *            the message
	 */
	public BaseResponse(int code, String message)
	{
		this.code = code;
		this.message = message;
	}
	
	/**
	 * Instantiates a new base response.
	 *
	 * @param message the message
	 */
	public BaseResponse(String message)
	{
		this(ICommonConstants.RESPONSE_CODE_SUCCESS, message);
	}

	/**
	 * Gets the  Response code.
	 *
	 * @return the  Response code
	 */
	public int getCode()
	{
		return code;
	}

	/**
	 * Sets the  Response code.
	 *
	 * @param code the new  Response code
	 */
	public void setCode(int code)
	{
		this.code = code;
	}

	/**
	 * Gets the  Response message.
	 *
	 * @return the  Response message
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * Sets the  Response message.
	 *
	 * @param message the new  Response message
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Code: ").append(code);
		builder.append(",").append("Message: ").append(message);

		builder.append("]");
		return builder.toString();
	}

}
