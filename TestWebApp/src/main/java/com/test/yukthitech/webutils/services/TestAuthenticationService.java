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

package com.test.yukthitech.webutils.services;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.test.yukthitech.webutils.Authorization;
import com.test.yukthitech.webutils.SecurityRole;
import com.test.yukthitech.webutils.TestUserDetails;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.webutils.common.models.ActiveUserModel;
import com.yukthitech.webutils.extensions.ExtensionEntityDetails;
import com.yukthitech.webutils.repository.UserEntity;
import com.yukthitech.webutils.repository.file.FileEntity;
import com.yukthitech.webutils.security.IAuthenticationService;
import com.yukthitech.webutils.security.ISecurityService;
import com.yukthitech.webutils.security.UserDetails;
import com.yukthitech.webutils.services.CurrentUserService;

/**
 * @author akiran
 *
 */
@Service
public class TestAuthenticationService implements ISecurityService, IAuthenticationService
{
	/** The user service. */
	@Autowired
	private TestUserService userService;
	
	/** The current user service. */
	@Autowired
	private CurrentUserService currentUserService;
	
	@Autowired
	private HttpServletRequest request;
	
	@Override
	public TestUserDetails authenticate(String userName, String password, Map<String, String> attrMap)
	{
		if(!"admin".equals(userName) || !"admin".equals(password))
		{
			return null;
		}
		
		return new TestUserDetails(userService.getUserId(), CommonUtils.toSet(SecurityRole.ADMIN, SecurityRole.CLIENT_ADMIN), 4321L);
	}

	@Override
	public boolean isAuthorized(Method method)
	{
		Authorization authorization = method.getAnnotation(Authorization.class);
		
		//if target method is not secured, return true
		if(authorization == null)
		{
			return true;
		}
		
		//check if current user has at least one role from required roles, if found return true 
		Set<SecurityRole> userRoles = ((TestUserDetails)currentUserService.getCurrentUserDetails()).getRoles();
		
		for(SecurityRole role : authorization.value())
		{
			if(userRoles.contains(role))
			{
				return true;
			}
		}
		
		//if user does not have any of required roles
		return false;
	}

	@Override
	public boolean isExtensionAuthorized(ExtensionEntityDetails extensionPoint)
	{
		return true;
	}

	@Override
	public void addSecurityCustomization(FileEntity fileEntity)
	{
	}

	@Override
	public boolean isAuthorized(FileEntity fileEntity)
	{
		return true;
	}

	@Override
	public ActiveUserModel getActiverUser()
	{
		return new ActiveUserModel();
	}

	@Override
	public String getUserSpaceIdentity()
	{
		String custId = request.getHeader("customerId");
		return (custId != null && custId.trim().length() > 0) ? "Cust-" + custId : "admin";
	}

	@Override
	public UserDetails getUserDetailsFor(UserEntity userEntity)
	{
		return null;
	}
}
