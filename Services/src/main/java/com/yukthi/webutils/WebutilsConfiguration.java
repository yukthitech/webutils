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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;

import com.yukthi.utils.exceptions.InvalidStateException;
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
	 * Web-application roles enum. Which in turn can be used for authorization on service methods
	 */
	private Class<? extends Enum<?>> rolesEnumType;
	
	/**
	 * Secret key used to enrypt/decrypt user details by auth services
	 */
	private String secretKey;
	
	/**
	 * User details type to be used
	 */
	@SuppressWarnings("rawtypes")
	private Class<? extends UserDetails> userDetailsType = UserDetails.class;
	
	/**
	 * Webutils authentication/authorization is enabled or not
	 */
	private boolean enableAuth = true;
	
	/** The authorization annotation. */
	private Class<? extends Annotation> authorizationAnnotation;
	
	/**
	 * Session timeout in minutes
	 */
	private int sessionTimeOutInMin = 3;
	
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

		if(!enableAuth)
		{
			return;
		}
		
		if(userDetailsType == null)
		{
			throw new IllegalStateException("No user-details-type is specified in Web-utils-configurationn bean. It is mandatory when auth is enabled.");
		}
		
		if(rolesEnumType == null)
		{
			throw new IllegalStateException("No roles-enum-type is specified in Web-utils-configurationn bean. It is mandatory when auth is enabled.");
		}
		
		if(secretKey == null)
		{
			throw new IllegalStateException("No secret key is specified in Web-utils-configurationn bean. It is mandatory when auth is enabled.");
		}
		
		if(authorizationAnnotation == null)
		{
			throw new IllegalStateException("No authroization annotation specified. It is mandatory when auth is enabled.");
		}

		try
		{
			Method valueMethod = authorizationAnnotation.getDeclaredMethod("value");
	
			if(valueMethod == null || !valueMethod.getReturnType().isArray() && !rolesEnumType.equals(valueMethod.getReturnType().getComponentType()) )
			{
				throw new InvalidStateException("Invalid authorization annotation '{}' specified. Authorization annotation should have value method "
						+ "and its return type should be roles type '{}' array", 
						authorizationAnnotation.getClass().getName(), rolesEnumType.getName());
			}
		}catch(NoSuchMethodException | SecurityException ex)
		{
			throw new IllegalStateException("An error occurred while fetching authorization annotation details", ex);
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
	 * Gets the web-application roles enum.
	 *
	 * @return the web-application roles enum
	 */
	public Class<? extends Enum<?>> getRolesEnumType()
	{
		return rolesEnumType;
	}

	/**
	 * Sets the web-application roles enum.
	 *
	 * @param rolesEnumType the new web-application roles enum
	 */
	public void setRolesEnumType(Class<? extends Enum<?>> rolesEnumType)
	{
		if(!Enum.class.isAssignableFrom(rolesEnumType))
		{
			throw new IllegalArgumentException("Non-enum is specified as roles type - " + rolesEnumType.getName());
		}
		
		this.rolesEnumType = rolesEnumType;
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
	@SuppressWarnings("rawtypes")
	public Class<? extends UserDetails> getUserDetailsType()
	{
		return userDetailsType;
	}

	/**
	 * Sets the user details type to be used.
	 *
	 * @param userDetailsType the new user details type to be used
	 */
	public void setUserDetailsType(Class<? extends UserDetails<?>> userDetailsType)
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
	public boolean isEnableAuth()
	{
		return enableAuth;
	}

	/**
	 * Sets the webutils authentication/authorization is enabled or not.
	 *
	 * @param enableAuth the new webutils authentication/authorization is enabled or not
	 */
	public void setEnableAuth(boolean enableAuth)
	{
		this.enableAuth = enableAuth;
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
	 * Gets the authorization annotation.
	 *
	 * @return the authorization annotation
	 */
	public Class<? extends Annotation> getAuthorizationAnnotation()
	{
		return authorizationAnnotation;
	}

	/**
	 * Sets the authorization annotation.
	 *
	 * @param authorizationAnnotation the new authorization annotation
	 */
	public void setAuthorizationAnnotation(Class<? extends Annotation> authorizationAnnotation)
	{
		this.authorizationAnnotation = authorizationAnnotation;
	}
}
