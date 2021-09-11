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

package com.yukthitech.webutils.common.models.auth;

import java.util.Map;

import com.yukthitech.validation.annotations.NotEmpty;
import com.yukthitech.webutils.common.annotations.IgnoreField;
import com.yukthitech.webutils.common.annotations.Model;

/**
 * Request to reset password.
 * 
 * @author akiran
 */
@Model
public class ResetPasswordRequest
{
	/**
	 * Credentials - user name
	 */
	@NotEmpty
	private String userName;

	/**
	 * Extra attributes that can be used for customization of login process 
	 */
	@IgnoreField
	private Map<String, String> attributes;

	/**
	 * Gets the credentials - user name.
	 *
	 * @return the credentials - user name
	 */
	public String getUserName()
	{
		return userName;
	}

	/**
	 * Sets the credentials - user name.
	 *
	 * @param userName the new credentials - user name
	 */
	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	/**
	 * Gets the extra attributes that can be used for customization of login process.
	 *
	 * @return the extra attributes that can be used for customization of login process
	 */
	public Map<String, String> getAttributes()
	{
		return attributes;
	}

	/**
	 * Sets the extra attributes that can be used for customization of login process.
	 *
	 * @param attributes the new extra attributes that can be used for customization of login process
	 */
	public void setAttributes(Map<String, String> attributes)
	{
		this.attributes = attributes;
	}
}
