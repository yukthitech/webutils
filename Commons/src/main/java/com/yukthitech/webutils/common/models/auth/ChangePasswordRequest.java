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

import com.yukthitech.validation.annotations.MaxLen;
import com.yukthitech.validation.annotations.MinLen;
import com.yukthitech.validation.annotations.NotEmpty;
import com.yukthitech.webutils.common.annotations.Model;

/**
 * Request to change password.
 * 
 * @author akiran
 */
@Model
public class ChangePasswordRequest
{
	/**
	 * Current password.
	 */
	@NotEmpty
	@MinLen(1)
	@MaxLen(50)
	private String currentPassword;
	
	/**
	 * New password to set.
	 */
	@NotEmpty
	@MinLen(1)
	@MaxLen(50)
	private String newPassword;

	/**
	 * Gets the current password.
	 *
	 * @return the current password
	 */
	public String getCurrentPassword()
	{
		return currentPassword;
	}

	/**
	 * Sets the current password.
	 *
	 * @param currentPassword the new current password
	 */
	public void setCurrentPassword(String currentPassword)
	{
		this.currentPassword = currentPassword;
	}

	/**
	 * Gets the new password to set.
	 *
	 * @return the new password to set
	 */
	public String getNewPassword()
	{
		return newPassword;
	}

	/**
	 * Sets the new password to set.
	 *
	 * @param newPassword the new new password to set
	 */
	public void setNewPassword(String newPassword)
	{
		this.newPassword = newPassword;
	}
}
