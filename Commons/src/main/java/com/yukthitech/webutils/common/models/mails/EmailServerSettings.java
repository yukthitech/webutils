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
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Settings or information required for sending and reading the mails.
 * @author akiran
 */
@Data
@Accessors(chain = true)
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
	 * Flag indicating id SSL should be enabled.
	 */
	private boolean enableSsl = false;
	
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
		props.put("mail.smtp.ssl.enable", "" + enableSsl);
		
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
