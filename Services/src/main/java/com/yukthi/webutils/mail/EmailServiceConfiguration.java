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
package com.yukthi.webutils.mail;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;

/**
 * Configuration required by Email Service
 * @author akiran
 */
public class EmailServiceConfiguration
{
	
	/** The Constant PROP_SMTP_HOST. */
	public static final String PROP_SMTP_HOST = "mail.smtp.host";
	
	/** The Constant PROP_SMTP_PORT. */
	public static final String PROP_SMTP_PORT = "mail.smtp.port";
	
	/** The Constant PROP_USE_AUTH. */
	public static final String PROP_USE_AUTH = "mail.smtp.auth";
	
	/** The Constant PROP_ENABLE_TTLS. */
	public static final String PROP_ENABLE_TTLS = "mail.smtp.starttls.enable";

	/**
	* Smtp host  
	*/
	private String smtpHost;
	
	/**
	 * Smtp port
	 */
	private Integer smtpPort;

	/**
	 * Flag to indicate whether authentication to be used
	 */
	private boolean useAuthentication = false;
	
	/**
	 * User name for authentication
	 */
	private String userName;
	
	/**
	 * Password for authentication
	 */
	private String password;

	/** The enable ttls. */
	private boolean enableTtls = false;
	
	/**
	* Template resources which gives templates for email service
	*/
	private List<String> templateResources;

	/**
	 * Gets the smtp host.
	 *
	 * @return the smtp host
	 */
	public String getSmtpHost()
	{
		return smtpHost;
	}

	/**
	 * Sets the smtp host.
	 *
	 * @param smtpHost the new smtp host
	 */
	public void setSmtpHost(String smtpHost)
	{
		this.smtpHost = smtpHost;
	}

	/**
	 * Gets the smtp port.
	 *
	 * @return the smtp port
	 */
	public Integer getSmtpPort()
	{
		return smtpPort;
	}

	/**
	 * Sets the smtp port.
	 *
	 * @param smtpPort the new smtp port
	 */
	public void setSmtpPort(Integer smtpPort)
	{
		this.smtpPort = smtpPort;
	}

	/**
	 * Checks if is flag to indicate whether authentication to be used.
	 *
	 * @return the flag to indicate whether authentication to be used
	 */
	public boolean isUseAuthentication()
	{
		return useAuthentication;
	}

	/**
	 * Sets the flag to indicate whether authentication to be used.
	 *
	 * @param useAuthentication the new flag to indicate whether authentication to be used
	 */
	public void setUseAuthentication(boolean useAuthentication)
	{
		this.useAuthentication = useAuthentication;
	}

	/**
	 * Gets the user name for authentication.
	 *
	 * @return the user name for authentication
	 */
	public String getUserName()
	{
		return userName;
	}

	/**
	 * Sets the user name for authentication.
	 *
	 * @param userName the new user name for authentication
	 */
	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	/**
	 * Gets the password for authentication.
	 *
	 * @return the password for authentication
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * Sets the password for authentication.
	 *
	 * @param password the new password for authentication
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

	/**
	 * Checks if is enable ttls.
	 *
	 * @return true, if is enable ttls
	 */
	public boolean isEnableTtls()
	{
		return enableTtls;
	}

	/**
	 * Sets the enable ttls.
	 *
	 * @param enableTtls the new enable ttls
	 */
	public void setEnableTtls(boolean enableTtls)
	{
		this.enableTtls = enableTtls;
	}
	
	/**
	 * Gets the template resources which gives templates for email service.
	 *
	 * @return the template resources which gives templates for email service
	 */
	public List<String> getTemplateResources()
	{
		if(templateResources == null)
		{
			return Collections.emptyList();
		}
		
		return templateResources;
	}

	/**
	 * Sets the template resources which gives templates for email service.
	 *
	 * @param templateResources the new template resources which gives templates for email service
	 */
	public void setTemplateResources(List<String> templateResources)
	{
		this.templateResources = templateResources;
	}

	/**
	* Validates required configuration params are provided
	*/
	@PostConstruct
	private void validate()
	{
		if(StringUtils.isEmpty(smtpHost))
		{
			throw new IllegalStateException("No SMTP host is provided");
		}

		if(useAuthentication)
		{
			if(StringUtils.isEmpty(userName) || StringUtils.isEmpty(password))
			{
				throw new IllegalStateException("No username/password is provided");
			}
		}
	}

	/**
	* Converts this configuration into properties compatible with java-mail
	*
	* @return
	*/
	public Properties toProperties()
	{
		Properties props = new Properties();
		
		if(useAuthentication)
		{
			props.put(PROP_USE_AUTH, "true");
		}
		
		if(enableTtls)
		{
			props.put(PROP_ENABLE_TTLS, "true");
		}
		
		props.put(PROP_SMTP_HOST, smtpHost);
		
		if(smtpPort != null)
		{
			props.put(PROP_SMTP_PORT, "" + smtpPort);
		}
		
		return props;
	}
}
