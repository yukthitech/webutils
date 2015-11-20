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

package com.yukthi.webutils;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.yukthi.webutils.common.IWebUtilsCommonConstants;
import com.yukthi.webutils.security.UserDetails;

/**
 * Configurations to be specified by web-applications using this web-utils
 * framework
 * 
 * @author akiran
 */
public class WebutilsConfiguration
{
	/**
	 * Flag indicating whether entity extension fields should be supported.
	 * Enabled by default. If enabled, creates required tables and entities for
	 * extensions
	 */
	private boolean extensionsRequired = true;

	/**
	 * Base packages of the web-application. This configuration is mandatory to auto load different services.
	 * Example - com.yukthi This will be used by scanning services to load
	 * different services automatically.
	 */
	private List<String> basePackages;
	
	/**
	 * Secret key used to encrypt/decrypt user details by auth services
	 */
	private String secretKey;
	
	/**
	 * User details type to be used
	 */
	private Class<? extends UserDetails> userDetailsType = UserDetails.class;
	
	/**
	 * Webutils authentication/authorization is enabled or not
	 */
	private boolean authEnabled = true;
	
	/**
	 * Session timeout in minutes
	 */
	private int sessionTimeOutInMin = 3;
	
	/**
	 * Time after which session expires even if active. If non-negative or zero, session never gets expired.
	 */
	private int sessionExpiryInMin = -1;
	
	/**
	 * Date format to be used for internal data exchange between client and server
	 */
	private SimpleDateFormat dateFormat = IWebUtilsCommonConstants.DEFAULT_DATE_FORMAT;
	
	/**
	 * Validte.
	 */
	@PostConstruct
	public void validte()
	{
		if(CollectionUtils.isEmpty(basePackages))
		{
			throw new IllegalStateException("No base package(s) specified in Web-utils-configurationn bean.");
		}

		if(!authEnabled)
		{
			return;
		}
		
		if(userDetailsType == null)
		{
			throw new IllegalStateException("No user-details-type is specified in Web-utils-configurationn bean. It is mandatory when auth is enabled.");
		}
		
		if(StringUtils.isBlank(secretKey))
		{
			throw new IllegalStateException("No secret key is specified in Web-utils-configurationn bean. It is mandatory when auth is enabled.");
		}
	}

	/**
	 * Checks whether entity extension fields should be supported.
	 *
	 * @return the flag indicating whether entity extension fields should be supported
	 */
	public boolean isExtensionsRequired()
	{
		return extensionsRequired;
	}

	/**
	 * Sets the flag indicating whether entity extension fields should be supported.
	 *
	 * @param extensionsRequired the new flag indicating whether entity extension fields should be supported
	 */
	public void setExtensionsRequired(boolean extensionsRequired)
	{
		this.extensionsRequired = extensionsRequired;
	}

	/**
	 * Gets the base packages of the web-application.
	 *
	 * @return the base packages of the web-application
	 */
	public List<String> getBasePackages()
	{
		return basePackages;
	}

	/**
	 * Sets the base packages of the web-application.
	 *
	 * @param basePackages the new base packages of the web-application
	 */
	public void setBasePackages(List<String> basePackages)
	{
		this.basePackages = basePackages;
	}

	/**
	 * Gets the secret key used to enrypt/decrypt user details by auth services.
	 *
	 * @return the secret key used to enrypt/decrypt user details by auth services
	 */
	public String getSecretKey()
	{
		return secretKey;
	}

	/**
	 * Sets the secret key used to enrypt/decrypt user details by auth services. Secret key length should be 16.
	 *
	 * @param secretKey the new secret key used to enrypt/decrypt user details by auth services
	 */
	public void setSecretKey(String secretKey)
	{
		if(secretKey.length() != 16)
		{
			throw new IllegalArgumentException("Secret key should be of length 16");
		}
		
		this.secretKey = secretKey;
	}

	/**
	 * Gets the user details type to be used.
	 *
	 * @return the user details type to be used
	 */
	public Class<? extends UserDetails> getUserDetailsType()
	{
		return userDetailsType;
	}

	/**
	 * Sets the user details type to be used.
	 *
	 * @param userDetailsType the new user details type to be used
	 */
	public void setUserDetailsType(Class<? extends UserDetails> userDetailsType)
	{
		if(!UserDetails.class.isAssignableFrom(userDetailsType))
		{
			throw new IllegalArgumentException("Non user-details element is specified as user details type - " + userDetailsType.getName());
		}
		
		this.userDetailsType = userDetailsType;
	}

	/**
	 * Checks if is webutils authentication/authorization is enabled or not.
	 *
	 * @return the webutils authentication/authorization is enabled or not
	 */
	public boolean isAuthEnabled()
	{
		return authEnabled;
	}

	/**
	 * Sets the webutils authentication/authorization is enabled or not.
	 *
	 * @param enableAuth the new webutils authentication/authorization is enabled or not
	 */
	public void setAuthEnabled(boolean enableAuth)
	{
		this.authEnabled = enableAuth;
	}

	/**
	 * Gets the session timeout in minutes.
	 *
	 * @return the session timeout in minutes
	 */
	public int getSessionTimeOutInMin()
	{
		return sessionTimeOutInMin;
	}

	/**
	 * Sets the session timeout in minutes. Time out value should be greater than or equal to 2 mins.
	 *
	 * @param sessionTimeOutInMin the new session timeout in minutes
	 */
	public void setSessionTimeOutInMin(int sessionTimeOutInMin)
	{
		if(sessionTimeOutInMin < 2)
		{
			throw new IllegalArgumentException("Session time can not be less than 2 mins - " + sessionTimeOutInMin);
		}
		
		this.sessionTimeOutInMin = sessionTimeOutInMin;
	}
	
	/**
	 * Sets the date format to be used for internal data exchange between client and server.
	 *
	 * @param dateFormat the new date format to be used for internal data exchange between client and server
	 */
	public void setDateFormat(String dateFormat)
	{
		this.dateFormat = new SimpleDateFormat(dateFormat);
	}
	
	/**
	 * Gets the date format to be used for internal data exchange between client and server.
	 *
	 * @return the date format to be used for internal data exchange between client and server
	 */
	public SimpleDateFormat getDateFormat()
	{
		return dateFormat;
	}

	/**
	 * Gets the time after which session expires even if active. If non-negative or zero, session never gets expired.
	 *
	 * @return the time after which session expires even if active
	 */
	public int getSessionExpiryInMin()
	{
		return sessionExpiryInMin;
	}

	/**
	 * Sets the time after which session expires even if active. If non-negative or zero, session never gets expired.
	 *
	 * @param sessionExpiryInMin the new time after which session expires even if active
	 */
	public void setSessionExpiryInMin(int sessionExpiryInMin)
	{
		this.sessionExpiryInMin = sessionExpiryInMin;
	}
	
	
}
