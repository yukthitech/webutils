package com.yukthi.webutils.mail;

import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.annotation.PostConstruct;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yukthi.utils.exceptions.InvalidArgumentException;
import com.yukthi.utils.exceptions.InvalidStateException;

/**
 * Service to send mails
 * 
 * @author akiran
 */
@Service
public class EmailService
{
	@Autowired
	private EmailServiceConfiguration configuration;
	private Properties configProperties;

	/**
	 * Post construct method to get config properties
	 */
	@PostConstruct
	private void init()
	{
		// get java mail properties from configuration
		configProperties = configuration.toProperties();
	}

	/**
	 * Create new java mail session with the configuration provided to the
	 * service
	 *
	 * @return
	 */
	private Session newSession()
	{
		Session mailSession = null;

		//if authentication needs to be done provide user name and password
		if(configuration.isUseAuthentication())
		{
			mailSession = Session.getInstance(configProperties, new Authenticator()
			{
				protected PasswordAuthentication getPasswordAuthentication()
				{
					return new PasswordAuthentication(configuration.getUserName(), configuration.getPassword());
				}
			});
		}
		else
		{
			mailSession = Session.getInstance(configProperties);
		}

		return mailSession;
	}

	/**
	* Converts provided list of email string to InternetAddress objects
	*
	* @param ids
	* @return
	* @throws AddressException
	*/
	private InternetAddress[] convertToInternetAddress(String listName, String ids[]) throws AddressException
	{
		InternetAddress[] res = new InternetAddress[ids.length];

		for(int i = 0; i < ids.length; i++)
		{
			try
			{
				res[i] = new InternetAddress(ids[i]);
			}catch(Exception ex)
			{
				throw new InvalidArgumentException(ex, "An error occurred while parsing email-id {} from {} list", ids[i], listName);
			}
		}

		return res;
	}

	/**
	* Checks if provided string array is null or empty
	*
	* @param str
	* @return
	*/
	private boolean isEmpty(String str[])
	{
		return(str == null || str.length == 0);
	}

	/**
	 * Builds the mail message from specified email data
	 * @param emailData
	 * @return
	 * @throws AddressException
	 * @throws MessagingException
	 */
	private Message buildMessage(EmailData emailData) throws AddressException, MessagingException
	{
		if(isEmpty(emailData.getToList()) && isEmpty(emailData.getCcList()) && isEmpty(emailData.getBccList()))
		{
			throw new InvalidArgumentException("No recipient email id specified in any of the email list");
		}
		
		//start new session
		Session mailSession = newSession();

		// build the mail message
		Message message = new MimeMessage(mailSession);

		try
		{
			message.setFrom(new InternetAddress(configuration.getUserName()));
		}catch(Exception ex)
		{
			throw new InvalidArgumentException("An error occurred while parsing from mail id - {}", emailData.getFromId());
		}

		//set recipients mail lists
		if(!isEmpty(emailData.getToList()))
		{
			message.setRecipients(Message.RecipientType.TO, convertToInternetAddress("To", emailData.getToList()));
		}

		if(!isEmpty(emailData.getCcList()))
		{
			message.setRecipients(Message.RecipientType.CC, convertToInternetAddress("CC", emailData.getCcList()));
		}

		if(!isEmpty(emailData.getBccList()))
		{
			message.setRecipients(Message.RecipientType.BCC, convertToInternetAddress("BCC", emailData.getBccList()));
		}

		//set the subject
		message.setSubject(emailData.getSubject());
		message.setSentDate(new Date());
		
		//create multi part message
		Multipart multiPart = new MimeMultipart();
		
		//add body to multi part
		BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(emailData.getContent(), "text/html");
		
		multiPart.addBodyPart(messageBodyPart);
		
		//add files if any
		if(emailData.getAttachments() != null)
		{
			FileAttachment attachments[] = emailData.getAttachments();
			FileDataSource fileSource = null;
			MimeBodyPart fileBodyPart = null;
			
			for(FileAttachment attachment : attachments)
			{
				fileBodyPart = new MimeBodyPart();
				fileSource = new FileDataSource(attachment.getFile());
				
				fileBodyPart.setDataHandler(new DataHandler(fileSource));
				fileBodyPart.setFileName(attachment.getFileName());
				
				multiPart.addBodyPart(fileBodyPart);
			}
		}
		
		//set the multi part message as content
		message.setContent(multiPart);
		
		return message;
	}

	/**
	 * Sends the specified email message
	 * @param email
	 */
	public void sendEmail(EmailData email)
	{
		try
		{
			// Build mail message object
			Message message = buildMessage(email);

			// send the message
			Transport.send(message);
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while sending email - {}", email);
		}
	}
	
}
