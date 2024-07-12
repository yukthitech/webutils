/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yukthitech.webutils;

import java.util.List;

import com.yukthitech.utils.exceptions.UtilsException;
import com.yukthitech.webutils.common.IWebUtilsCommonConstants;
import com.yukthitech.webutils.common.models.FieldError;

/**
 * Thrown when request is not valid.
 */
public class InvalidRequestException extends UtilsException
{
	private static final long serialVersionUID = 1L;

	/**
	 * Status code to be sent as part of response.
	 */
	private int statusCode = IWebUtilsCommonConstants.RESPONSE_CODE_INVALID_REQUEST;
	
	/**
	 * Field wise errors.
	 */
	private List<FieldError> fieldErrors;
	
	public InvalidRequestException(String message, Object... args)
	{
		super(message, args);
	}

	public InvalidRequestException(int statusCode, String message, Object... args)
	{
		super(message, args);
		this.statusCode = statusCode;
	}

	public InvalidRequestException(List<FieldError> fieldErrors, String message, Object... args)
	{
		super(message, args);
		this.fieldErrors = fieldErrors;
	}

	public int getStatusCode()
	{
		return statusCode;
	}
	
	public InvalidRequestException setStatusCode(int statusCode)
	{
		this.statusCode = statusCode;
		return this;
	}

	public List<FieldError> getFieldErrors()
	{
		return fieldErrors;
	}

	public InvalidRequestException setFieldErrors(List<FieldError> fieldErrors)
	{
		this.fieldErrors = fieldErrors;
		return this;
	}
}
