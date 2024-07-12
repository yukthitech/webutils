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

package com.yukthitech.webutils.common;

import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;

/**
 * Different response codes.
 * @author akiran
 */
public interface IWebUtilsCommonConstants
{
	/**
	 * Success code to be used for successful request processing.
	 */
	public int RESPONSE_CODE_SUCCESS = 0;
	
	/**
	 * Default success message response.
	 */
	public String DEF_RESPONSE_SUCCES_MESSAGE = "Operation was successful.";
	
	/**
	 * Response code to be used when input request validation failed.
	 */
	public int RESPONSE_CODE_INVALID_REQUEST = 4400;
	
	/**
	 * Response code when invalid/wrong value is specified.
	 */
	public int RESPONSE_CODE_INVALID_VALUE = 4401;
	
	/**
	 * Response code when expired value is specified.
	 */
	public int RESPONSE_CODE_EXPIRED_VALUE = 4402;

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

	/**
	 * Used to define multi part name for models, when attachments are expected
	 */
	public String MULTIPART_DEFAULT_PART = "default";
	
	public String AUTH_GROUP_URI = "/auth";
	
	/**
	 * Path for fetching active user details
	 */
	public String ACTION_GROUP_URI = "/actions";
	public String FETCH_URI_PATH = "/fetch";
	
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
	
	/**
	 * Phone pattern string.
	 */
	public String PATTERN_PHONE = "[\\d\\ \\(\\)]+";
	
	/**
	 * Error message for name pattern.
	 */
	public String PATTERN_MSSG_PHONE = "Phone number can contain only numbers.";

	/**
	 * Email pattern string.
	 */
	public String PATTERN_EMAIL = "[\\w\\.\\-]+\\@[\\w\\.\\-]+\\.[\\w]+";
	
	/**
	 * Error message for mail pattern.
	 */
	public String PATTERN_MSSG_EMAIL = "Mail-id can contain alpha numeric characters or dot or hyphen.";

	/** 
	 * The pattern url link to save the documents link. 
	 **/
	public String PATTERN_URL_LINK = "\\w+\\:.+";
	
	/**
	 * Pattern to be used for name fields.
	 */
	public String PATTERN_NAME = "[a-zA-Z\\s]+";
	
	/**
	 * Error message for name pattern.
	 */
	public String PATTERN_MSSG_NAME = "Name can contain only alphabets.";
	
	/**
	 * Pattern which is not suppose to be found in names.
	 */
	public String MIS_PATTERN_NAME = "\\s+";

	/**
	 * Error message for name pattern.
	 */
	public String MIS_PATTERN_MSSG_NAME = "Name should not contain more than one continous space.";

	/**
	 * Alert Flag indicating if confirmation is required for alert.
	 */
	public int ALERT_FLAG_CONFIRMATION_REQUIRED = 0b1;
	
	/**
	 * Alert Flag indicating if an alert is a silent alert and end user should not be 
	 * disturbed with this alert.
	 */
	public int ALERT_FLAG_SILENT_ALERT = 0b10;
	
	/**
	 * Alert Flag indicating if an alert is confirmation alert.
	 */
	public int ALERT_FLAG_CONFIRMATION_ALERT = 0b100;
	
	/**
	 * Alert Flag (1 of 3) that can be used for app specific purpose.
	 */
	public int ALERT_FLAG_APP_SPECIFIC_1 = 0b1000;
	
	/**
	 * Alert Flag (2 of 3) that can be used for app specific purpose.
	 */
	public int ALERT_FLAG_APP_SPECIFIC_2 = 0b10000;

	/**
	 * Alert Flag (3 of 3) that can be used for app specific purpose.
	 */
	public int ALERT_FLAG_APP_SPECIFIC_3 = 0b100000;

	/**
	 * Mapper that will be used to convert objects to and from json.
	 */
	public ObjectMapper OBJECT_MAPPER_WITH_TYPE = new ObjectMapper()
	{
		private static final long serialVersionUID = 1L;
		{
			enableDefaultTyping(DefaultTyping.NON_FINAL, As.PROPERTY);
		}
	};

	/**
	 * Mapper that will be used to convert objects to and from json.
	 */
	public ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	public int CAPTCHA_LENGTH = 5;
}
