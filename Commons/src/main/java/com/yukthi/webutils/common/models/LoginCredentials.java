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

import com.yukthi.validation.annotations.NotEmpty;
import com.yukthi.webutils.common.annotations.Model;

/**
 * Credentials for login
 * 
 * @author akiran
 */
@Model
public class LoginCredentials
{
	/**
	 * Credentials - user name
	 */
	@NotEmpty
	private String userName;

	/**
	 * Credentials - password
	 */
	@NotEmpty
	private String password;

	/**
	 * Instantiates a new login credentials.
	 */
	public LoginCredentials()
	{}

	/**
	 * Instantiates a new login credentials.
	 *
	 * @param userName the user name
	 * @param password the password
	 */
	public LoginCredentials(String userName, String password)
	{
		this.userName = userName;
		this.password = password;
	}

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
	 * Gets the credentials - password.
	 *
	 * @return the credentials - password
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * Sets the credentials - password.
	 *
	 * @param password the new credentials - password
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

}
