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

package com.yukthitech.webutils.common.models.mails;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.validation.annotations.NotEmpty;
import com.yukthitech.webutils.common.annotations.Model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Settings or information required for sending and reading the mails.
 * @author akiran
 */
@Model
public class EmailServerSettings
{
	/**
	 * Property name for setting smtp host.
	 */
	public static final String PROP_SMTP_HOST = "mail.smtp.host";
	
	/**
	 * Host name property to the name you want to use for the HELO command.
	 */
	public static final String PROP_SMTP_LOCAL_HOST = "mail.smtp.localhost";

	/**
	 * Property for setting smtp port.
	 */
	public static final String PROP_SMTP_PORT = "mail.smtp.port";
	
	/**
	 * Property indicating if auth is enabled or not.
	 */
	public static final String PROP_USE_AUTH = "mail.smtp.auth";
	
	/**
	 * Property indicating if TTLS should be used.
	 */
	public static final String PROP_ENABLE_TTLS = "mail.smtp.starttls.enable";
	
	/**
	* Smtp host.
	*/
	@NotNull
	@Size(min = 3)
	private String smtpHost;
	
	/**
	* Host name property to the name you want to use for the HELO command.
	*/
	@Size(min = 3)
	private String smtpLocalHost;

	/**
	 * Smtp port.
	 */
	@NotNull
	private Integer smtpPort;

	/**
	 * Flag to indicate whether authentication to be used.
	 */
	private boolean useAuthentication = false;
	
	/**
	 * User name for authentication.
	 */
	@NotNull
	@Size(min = 3)
	private String userName;
	
	/**
	 * Password for authentication.
	 */
	@NotNull
	@Size(min = 3)
	private String password;

	/**
	 * Flag indicating if ttls should be enabled.
	 */
	private boolean enableTtls = false;
	
	/**
	 * Host address from where mail can be read or deleted.
	 */
	@NotNull
	@Size(min = 3)
	private String imapHost;
	
	@NotNull
	private Integer imapPort;
	
	/**
	 * Flag indicating if TLS v2 related config has to be enabled.
	 */
	private boolean enableTlsV2;
	
	/**
	 * Folders from which mails needs to be accessed.
	 */
	@NotEmpty
	private List<String> folderNames = Arrays.asList("INBOX");
	
	/**
	 * Sent folder to which mails being sent should be copied.
	 */
	private String sentFolder = "Sent";
	
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
	 * Gets the host name property to the name you want to use for the HELO command.
	 *
	 * @return the host name property to the name you want to use for the HELO command
	 */
	public String getSmtpLocalHost()
	{
		return smtpLocalHost;
	}

	/**
	 * Sets the host name property to the name you want to use for the HELO command.
	 *
	 * @param smtpLocalHost the new host name property to the name you want to use for the HELO command
	 */
	public void setSmtpLocalHost(String smtpLocalHost)
	{
		this.smtpLocalHost = smtpLocalHost;
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
	 * Checks if is flag indicating if ttls should be enabled.
	 *
	 * @return the flag indicating if ttls should be enabled
	 */
	public boolean isEnableTtls()
	{
		return enableTtls;
	}

	/**
	 * Sets the flag indicating if ttls should be enabled.
	 *
	 * @param enableTtls the new flag indicating if ttls should be enabled
	 */
	public void setEnableTtls(boolean enableTtls)
	{
		this.enableTtls = enableTtls;
	}
	
	public String getImapHost()
	{
		return imapHost;
	}

	public void setImapHost(String imapHost)
	{
		this.imapHost = imapHost;
	}

	public Integer getImapPort()
	{
		return imapPort;
	}

	public void setImapPort(Integer imapPort)
	{
		this.imapPort = imapPort;
	}

	/**
	 * Gets the folders from which mails needs to be accessed.
	 *
	 * @return the folders from which mails needs to be accessed
	 */
	public List<String> getFolderNames()
	{
		return folderNames;
	}

	/**
	 * Sets the folders from which mails needs to be accessed.
	 *
	 * @param folderNames the new folders from which mails needs to be accessed
	 */
	public void setFolderNames(List<String> folderNames)
	{
		this.folderNames = folderNames;
	}
	
	/**
	 * Gets the sent folder to which mails being sent should be copied.
	 *
	 * @return the sent folder to which mails being sent should be copied
	 */
	public String getSentFolder()
	{
		return sentFolder;
	}

	/**
	 * Sets the sent folder to which mails being sent should be copied.
	 *
	 * @param sentFolder the new sent folder to which mails being sent should be copied
	 */
	public void setSentFolder(String sentFolder)
	{
		this.sentFolder = sentFolder;
	}
	
	public boolean isEnableTlsV2()
	{
		return enableTlsV2;
	}

	public void setEnableTlsV2(boolean enableTlsV2)
	{
		this.enableTlsV2 = enableTlsV2;
	}

	/**
	* Validates required configuration params are provided.
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
	* Converts this configuration into properties compatible with java-mail.
	*
	* @return Java mail compatible properties.
	*/
	public Properties toProperties(boolean forImap)
	{
		Properties props = new Properties();
		
		props.put(PROP_USE_AUTH, "" + useAuthentication);
		props.put(PROP_ENABLE_TTLS, "" + enableTtls);
		props.put("mail.smtp.ssl.enable", "" + enableTtls);
		
		props.put(PROP_SMTP_HOST, forImap ? imapHost : smtpHost);
		props.put(PROP_SMTP_PORT, forImap ? "" + imapPort : "" + smtpPort);
		
		if(smtpLocalHost != null && smtpLocalHost.trim().length() > 0)
		{
			props.put(PROP_SMTP_LOCAL_HOST, smtpLocalHost);
		}
		
		if(enableTlsV2)
		{
			props.put("mail.smtp.ssl.protocols", "TLSv1.2");
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		}
		
		return props;
	}
}
