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

package com.yukthitech.webutils;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;

/**
 * Configurations to be specified by web-applications using this web-utils.
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
	 * Webutils authentication/authorization is enabled or not.
	 */
	private boolean authEnabled = true;
	
	/**
	 * Date format to be used for internal data exchange between client and server
	 */
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	
	/**
	 * Javascript based date format.
	 */
	private String jsDateFormat = "DD/MM/YYYY";
	
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
	 * Gets the javascript based date format.
	 *
	 * @return the javascript based date format
	 */
	public String getJsDateFormat()
	{
		return jsDateFormat;
	}

	/**
	 * Sets the javascript based date format.
	 *
	 * @param jsDateFormat the new javascript based date format
	 */
	public void setJsDateFormat(String jsDateFormat)
	{
		this.jsDateFormat = jsDateFormat;
	}
}
