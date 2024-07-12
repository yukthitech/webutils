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
package com.yukthitech.webutils.user;

import com.yukthitech.persistence.repository.annotations.Field;

/**
 * Encapsulation of user passwords.
 */
public class UserPasswords
{
	/**
	 * Id of the user.
	 */
	@Field("id")
	private Long id;
	
	/**
	 * Password of the user.
	 */
	@Field("password")
	private String password;
	
	/**
	 * Temporary password set as part of reset process.
	 */
	@Field("resetPassword")
	private String resetPassword;

	/**
	 * Gets the id of the user.
	 *
	 * @return the id of the user
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * Sets the id of the user.
	 *
	 * @param id the new id of the user
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * Gets the password of the user.
	 *
	 * @return the password of the user
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * Sets the password of the user.
	 *
	 * @param password the new password of the user
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

	/**
	 * Gets the temporary password set as part of reset process.
	 *
	 * @return the temporary password set as part of reset process
	 */
	public String getResetPassword()
	{
		return resetPassword;
	}

	/**
	 * Sets the temporary password set as part of reset process.
	 *
	 * @param resetPassword the new temporary password set as part of reset
	 *        process
	 */
	public void setResetPassword(String resetPassword)
	{
		this.resetPassword = resetPassword;
	}
}
