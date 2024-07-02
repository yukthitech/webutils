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
package com.yukthitech.webutils.common.verification;

import com.yukthitech.validation.annotations.Required;
import com.yukthitech.webutils.common.annotations.Model;

@Model
public class OtpVerificationRequest
{
	/**
	 * Token generated during otp sending.
	 */
	@Required
	private String token;
	
	/**
	 * Type of verification to be done. Eg: phone, email, etc.
	 */
	@Required
	private String type;
	
	/**
	 * Value to be verified. Eg: phone number, email id, etc.
	 */
	@Required
	private String value;
	
	/**
	 * Otp fed by user for verification.
	 */
	@Required
	private String otp;

	public String getToken()
	{
		return token;
	}

	public void setToken(String token)
	{
		this.token = token;
	}

	public String getOtp()
	{
		return otp;
	}

	public void setOtp(String otp)
	{
		this.otp = otp;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}
}
