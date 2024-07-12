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

package com.yukthitech.webutils.common.models;

import java.util.List;

import com.yukthitech.webutils.common.IWebUtilsCommonConstants;

/**
 * Base for all response classes to include response code and message.
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
	 * In case of error response, this can be used to specify field
	 * wise errors.
	 */
	private List<FieldError> fieldErrors;

	/**
	 * Instantiates a new base response.
	 */
	public BaseResponse()
	{
		this(IWebUtilsCommonConstants.RESPONSE_CODE_SUCCESS, IWebUtilsCommonConstants.DEF_RESPONSE_SUCCES_MESSAGE);
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
		this(IWebUtilsCommonConstants.RESPONSE_CODE_SUCCESS, message);
	}
	
	public BaseResponse(int code, String message, List<FieldError> fieldErrors)
	{
		this.code = code;
		this.message = message;
		this.fieldErrors = fieldErrors;
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
	
	public List<FieldError> getFieldErrors()
	{
		return fieldErrors;
	}

	public void setFieldErrors(List<FieldError> fieldErrors)
	{
		this.fieldErrors = fieldErrors;
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
