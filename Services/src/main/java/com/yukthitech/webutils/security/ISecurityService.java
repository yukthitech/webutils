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

package com.yukthitech.webutils.security;

import java.util.List;

import com.yukthitech.webutils.common.ContactDetails;
import com.yukthitech.webutils.extensions.ExtensionEntityDetails;
import com.yukthitech.webutils.repository.file.FileEntity;

/**
 * Authentication service to be provided by the web-application to authenticate and authorize
 * the users.
 * @author akiran
 */
public interface ISecurityService
{
	/**
	 * Invoked to check if specified user is authorized to invoke specified method. This method is expected to read
	 * security annotations from the target method and cross check with specified roles and decide the authorization
	 * @param context Information about the controller method being invoked
	 * @return True, if user is authorized to invoke the method
	 */
	public boolean isAuthorized(SecurityInvocationContext context);
	
	/**
	 * This method should return true only if current user is authorized to access specified file content.
	 * @param fileEntity File which needs to be checked for authorization
	 * @return True if current user is authorized to access specified file
	 */
	public boolean isAuthorized(FileEntity fileEntity);
	
	/**
	 * This method should return identity string to which user belongs. If the app does not have user spaces, this method
	 * can return null.
	 * This space is used to restrict operations to the specified user space.
	 * @return User space identity string.
	 */
	public default String getUserSpaceIdentity()
	{
		return "";
	}
	
	/**
	 * Invoked to check if specified extension can be accessed by specified user.
	 * @param extensionPoint Extension point details
	 * @return true if specified user is authorized to access specified extension
	 */
	public boolean isExtensionAuthorized(ExtensionEntityDetails extensionPoint);
	
	/**
	 * This method will be called before file entity is getting saved. This method can populated file entity's custom 
	 * fields with required information. This information can be used in checking for authorization during file access by user. 
	 * @param fileEntity File entity to which security customization can be done
	 */
	public void addSecurityCustomization(FileEntity fileEntity);
	
	/**
	 * Need to fetch contact details of user with specified role. This is used by certain services which
	 * need to send user details to other agents.
	 * @param role role of users to fetch
	 * @return matching user contact details.
	 */
	public default List<ContactDetails> fetchUserContactDetails(Object role)
	{
		return null;
	}
}
