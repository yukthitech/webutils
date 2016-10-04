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

package com.yukthi.webutils.services;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yukthi.webutils.IWebUtilsInternalConstants;
import com.yukthi.webutils.repository.WebutilsEntity;
import com.yukthi.webutils.repository.UserEntity;
import com.yukthi.webutils.security.UserDetails;

/**
 * Context user related services.
 * @author akiran
 */
@Service
public class CurrentUserService
{
	private static Logger logger = LogManager.getLogger(CurrentUserService.class);
	
	/**
	 * Autowired current request object.
	 */
	@Autowired
	private HttpServletRequest request;
	
	/**
	 * Internal active user to be used for populating tracking fields when request is not available.
	 */
	private UserDetails internalActiveUser;
	
	/**
	 * Used to set active user. Expected to be used for internal services like bootstrap loader.
	 * @param userDetails User details to be set.
	 */
	public void setInternalCurrentUser(UserDetails userDetails)
	{
		this.internalActiveUser = userDetails;
	}
	
	/**
	 * Fetches current user details from the request.
	 * @return Current user details
	 */
	public UserDetails getCurrentUserDetails()
	{
		try
		{
			return (UserDetails) request.getAttribute(IWebUtilsInternalConstants.REQ_ATTR_USER_DETAILS);
		}catch(Exception ex)
		{
			if(internalActiveUser != null)
			{
				return internalActiveUser;
			}
			
			logger.info("An error occurred while fetching user details from request - " + ex);
			return null;
		}
	}
	
	/**
	 * Sets the tracked fields during create operation.
	 * @param trackedEntity Entity needs to be tracked
	 */
	public void populateTrackingFieldForCreate(WebutilsEntity trackedEntity)
	{
		UserDetails userDetails = getCurrentUserDetails();

		//set date fields
		Date now = new Date();
		
		trackedEntity.setCreatedOn(now);
		trackedEntity.setUpdatedOn(now);
		
		//if user details are not available skip other fields
		if(userDetails == null)
		{
			return;
		}
		
		//set user fields
		long userId = userDetails.getUserId();
		trackedEntity.setCreatedBy(new UserEntity(userId));
		trackedEntity.setUpdatedBy(new UserEntity(userId));
	}

	/**
	 * Sets the tracked fields during update operation.
	 * @param trackedEntity Entity needs to be tracked
	 */
	public void populateTrackingFieldForUpdate(WebutilsEntity trackedEntity)
	{
		UserDetails userDetails = getCurrentUserDetails();

		//set date fields
		Date now = new Date();
		
		trackedEntity.setUpdatedOn(now);
		
		//if user details are not available skip other fields
		if(userDetails == null)
		{
			return;
		}
		
		//set user fields
		long userId = userDetails.getUserId();
		trackedEntity.setUpdatedBy(new UserEntity(userId));
	}
}
