package com.webutils.services.mail;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.webutils.common.mail.EmailServerSettings;
import com.webutils.common.mail.IMailCustomizer;
import com.webutils.services.common.FreeMarkerService;
import com.webutils.services.mail.template.MailTemplateEntity;
import com.webutils.services.mail.template.MailTemplateService;
import com.yukthitech.ccg.xml.DefaultParserHandler;
import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import jakarta.mail.Address;
import jakarta.mail.Authenticator;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

/**
 * Service to send and receive mails.
 * 
 * @author akiran
 */
public class EmailService
{
	private static Logger logger = LogManager.getLogger(EmailService.class);
	
	/**
	 * Default email server settings to be used.
	 */
	@Autowired
	private EmailServerSettings emailServerSettings;
	
	/**
	 * Service to access mail templates.
	 */
	@Autowired(required = false)
	private MailTemplateService mailTemplateService;

	/**
	 * Freemarker service to process email templates.
	 */
	@Autowired
	private FreeMarkerService freeMarkerService;
	
	/**
	 * Cache of email sessions used for efficiency.
	 */
	private Map<String, Session> sessionCache = new HashMap<>();

	/**
	 * Create new java mail session with the configuration provided to the
	 * service.
	 *
	 * @param settings
	 *            Settings to create session.
	 * @return newly created session.
	 */
	private Session newSession(EmailServerSettings settings)
	{
		String key = settings.getSmtpHost() + ":" + settings.getSmtpPort() + "@" + settings.getUserName();
		Session mailSession = sessionCache.get(key);
		
		if(mailSession != null)
		{
			return mailSession;
		}
		
		Properties configProperties = settings.toProperties();

		// if authentication needs to be done provide user name and password
		if(settings.isUseAuthentication())
		{
			mailSession = Session.getInstance(configProperties, new Authenticator()
			{
				protected PasswordAuthentication getPasswordAuthentication()
				{
					return new PasswordAuthentication(settings.getUserName(), settings.getPassword());
				}
			});
		}
		else
		{
			mailSession = Session.getInstance(configProperties);
		}

		sessionCache.put(key, mailSession);
		return mailSession;
	}

	/**
	 * Converts provided list of email string to InternetAddress objects.
	 * 
	 * @param listName
	 *            List name for which conversion is being done (Eg: TO, CC,
	 *            etc).
	 * @param ids
	 *            Ids to be converted.
	 * @return Converted Internet address list.
	 */
	private InternetAddress[] convertToInternetAddress(String listName, String ids[]) throws AddressException
	{
		InternetAddress[] res = new InternetAddress[ids.length];

		for(int i = 0; i < ids.length; i++)
		{
			try
			{
				res[i] = new InternetAddress(ids[i]);
			} catch(Exception ex)
			{
				throw new InvalidArgumentException("An error occurred while parsing email-id {} from {} list", ids[i], listName, ex);
			}
		}

		return res;
	}

	/**
	 * Checks if provided string array is null or empty.
	 *
	 * @param strLst
	 *            String array to check.
	 * @return True if null or empty.
	 */
	private boolean isEmpty(List<String> strLst)
	{
		return (strLst == null || strLst.isEmpty());
	}

	/**
	 * Adds attachments to specified mail.
	 * 
	 * @param multiPart
	 *            Multipart mail to which attachments needs to be attached.
	 * @param context
	 *            Context to be used which is expected have attachments.
	 */
	private void addAttachments(Multipart multiPart, List<MailAttachment> attachments) throws Exception
	{
		if(attachments == null)
		{
			return;
		}

		FileDataSource fileSource = null;
		MimeBodyPart fileBodyPart = null;

		for(MailAttachment fileAttachment : attachments)
		{
			fileBodyPart = new MimeBodyPart();
			fileSource = new FileDataSource(fileAttachment.getFileContent());

			fileBodyPart.setDataHandler(new DataHandler(fileSource));
			fileBodyPart.setFileName(fileAttachment.getFileName());
			fileBodyPart.setHeader("Content-ID", "<" + fileAttachment.getContentId() + ">");

			multiPart.addBodyPart(fileBodyPart);
		}
	}

	/**
	 * Splits the specified string using comma as delimiter and returns the
	 * resulted string list.
	 * 
	 * @param str
	 *            String to be converted.
	 * @return Converted list.
	 */
	private List<String> toList(String str)
	{
		if(str == null || str.trim().length() == 0)
		{
			return null;
		}

		String arr[] = str.trim().split("\\s*\\,\\s*");
		return Arrays.asList(arr);
	}
	
	/**
	 * Used to process template. If context is null, template will be returned directly without
	 * processing.
	 * @param name
	 * @param template
	 * @param context
	 * @return
	 */
	private String processTemplate(String name, String template, Object context)
	{
		if(context == null)
		{
			return template;
		}
		
		return freeMarkerService.processTemplate(name, template, context);
	}

	/**
	 * Builds the mail message from specified email data.
	 * 
	 * @param settings
	 *            Setti/ngs to be used.
	 * @param emailTemplate
	 *            Email data from which messaged needs to be built.
	 * @param context
	 *            Context to be used for freemarker expressions parsing.
	 * @return Converted message.
	 */
	private Message buildMessage(EmailServerSettings settings, MailTemplateEntity emailTemplate, Object context, List<MailAttachment> attachments) throws AddressException, MessagingException
	{
		// get the list of mail recipients
		String toStr = processTemplate(emailTemplate.getTemplateName() + ".to", emailTemplate.getToListTemplate(), context);
		String ccStr = processTemplate(emailTemplate.getTemplateName() + ".cc", emailTemplate.getCcListTemplate(), context);
		String bccStr = processTemplate(emailTemplate.getTemplateName() + ".cc", emailTemplate.getBccListTemplate(), context);

		String subject = processTemplate(emailTemplate.getTemplateName() + ".subject", emailTemplate.getSubjectTemplate(), context);
		String content = processTemplate(emailTemplate.getTemplateName() + ".content", emailTemplate.getContentTemplate(), context);

		List<String> toStrLst = toList(toStr);
		List<String> ccStrLst = toList(ccStr);
		List<String> bccStrLst = toList(bccStr);
		
		MailMessage mailMessage = new MailMessage();
		mailMessage.setToList(toStrLst);
		mailMessage.setCcList(ccStrLst);
		mailMessage.setBccList(bccStrLst);
		mailMessage.setSubject(subject);
		mailMessage.setBody(content);

		String fromId = settings.getUserName();
		
		if(context instanceof IMailCustomizer)
		{
			String customFromId = ((IMailCustomizer) context).getFromId();
			fromId = (customFromId != null) ? customFromId : fromId;
		}

		logger.debug("Setting mail details as: [\n\tTo: {}, \n\tCC: {}, \n\tBCC: {}, \n\tFrom: {}, \n\tSubject: {}]", toStrLst, ccStrLst, bccStrLst, fromId, subject);

		if(isEmpty(mailMessage.getToList()) && isEmpty(mailMessage.getCcList()) && isEmpty(mailMessage.getBccList()))
		{
			throw new InvalidArgumentException("No recipient email id specified in any of the email list");
		}

		// start new session
		Session mailSession = newSession(settings);

		// build the mail message
		Message message = new MimeMessage(mailSession);

		try
		{
			message.setFrom(new InternetAddress(fromId));
			message.setReplyTo(new Address[] {new InternetAddress(fromId)});
		} catch(Exception ex)
		{
			throw new InvalidArgumentException("An error occurred while parsing from mail id - {}", fromId);
		}
		
		int idx = fromId.indexOf('@');
		String domain = fromId.substring(idx + 1);
		
		message.addHeader("Message-ID", String.format("<%s@%s>", UUID.randomUUID().toString(), domain));
		message.addHeader("Thread-Topic", mailMessage.getSubject());
		message.addHeader("Thread-Index", UUID.randomUUID().toString());
		message.addHeader("Content-Language", "en-US");

		// set recipients mail lists
		if(!isEmpty(mailMessage.getToList()))
		{
			message.setRecipients(Message.RecipientType.TO, convertToInternetAddress("To", mailMessage.getToList().toArray(new String[0])));
		}

		if(!isEmpty(mailMessage.getCcList()))
		{
			message.setRecipients(Message.RecipientType.CC, convertToInternetAddress("CC", mailMessage.getCcList().toArray(new String[0])));
		}

		if(!isEmpty(mailMessage.getBccList()))
		{
			message.setRecipients(Message.RecipientType.BCC, convertToInternetAddress("BCC", mailMessage.getBccList().toArray(new String[0])));
		}

		// set the subject
		message.setSubject(mailMessage.getSubject());
		message.setSentDate(new Date());

		// create multi part message
		Multipart multiPart = new MimeMultipart();

		// add body to multi part
		BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(mailMessage.getBody(), "text/html");

		multiPart.addBodyPart(messageBodyPart);

		// add files if any
		try
		{
			addAttachments(multiPart, attachments);
		} catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while setting attachments", ex);
		}

		// set the multi part message as content
		message.setContent(multiPart);

		return message;
	}
	
	/**
	 * Copies message to sent folder.
	 *
	 * @param settings the settings
	 * @param message the message
	 */
	/*
	private void copyToSentFolder(EmailServerSettings settings, Message message)
	{
		Session mailSession = newSession(settings, true);
		
		try
		{
			Store store = mailSession.getStore("imap");
			store.connect();
			
			String folderPath[] = settings.getSentFolder().split("/");
			Folder folder = null;
			
			for(int i = 0; i < folderPath.length; i++)
			{
				if(folder == null)
				{
					folder = store.getFolder(folderPath[i]);
				}
				else
				{
					folder = folder.getFolder(folderPath[i]);
				}
			}
			
		    folder.open(Folder.READ_WRITE);
		    message.setFlag(Flag.SEEN, true);
		    folder.appendMessages(new Message[] {message});
		    store.close();
		}catch(Exception ex)
		{
			throw new InvalidStateException("Failed to copy mail to sent folder", ex);
		}
	}
	*/
	
	public void sendEmail(String templateName, Object context, List<MailAttachment> attachments)
	{
		MailTemplateEntity mailTemplateEntity = mailTemplateService.fetchByName(templateName);
		
		if(mailTemplateEntity == null)
		{
			throw new InvalidArgumentException("Non existing mail template specified: {}", templateName);
		}
		
		sendEmail(emailServerSettings, mailTemplateEntity, context, attachments);
	}

	public void sendEmailUsingRes(String resFile, Object context, List<MailAttachment> attachments)
	{
		try(InputStream inputStream = EmailService.class.getResourceAsStream(resFile))
		{
			DefaultParserHandler handler = new DefaultParserHandler();
			handler.setExpressionEnabled(false);
			
			MailTemplateEntity mailTemplateEntity = new MailTemplateEntity();
			XMLBeanParser.parse(inputStream, mailTemplateEntity, handler);

			sendEmail(emailServerSettings, mailTemplateEntity, context, attachments);
		} catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while sending email using resource file", ex);
		}
	}

	/**
	 * Sends the specified email message.
	 * 
	 * @param settings
	 *            Email server settings to be used.
	 * @param email
	 *            Email data to be used.
	 * @param context
	 *            Context to be used for processing.
	 */
	public void sendEmail(EmailServerSettings settings, MailTemplateEntity email, Object context, List<MailAttachment> attachments)
	{
		Message message = null;
		
		try
		{
			// Build mail message object
			message = buildMessage(settings, email, context, attachments);
			
			// send the message
			Transport.send(message);

			if(attachments != null)
			{
				attachments.forEach(attachment -> attachment.getFileContent().delete());
			}
		} catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while sending email - {}", email, ex);
		}
		
		/*
		 * COpy-to is taken care by most of SMTP servers themselves
		try
		{
			copyToSentFolder(settings, message);
		} catch(Exception ex)
		{
			logger.debug("An error occurred while copying mail to sent folder. Error: {}", "" + ex);
		}
		*/
	}
}
