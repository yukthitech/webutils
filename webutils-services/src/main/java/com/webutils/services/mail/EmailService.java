package com.webutils.services.mail;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.webutils.common.mail.EmailServerSettings;
import com.webutils.common.mail.IMailCustomizer;
import com.webutils.common.mail.template.MailTemplateConfiguration;
import com.webutils.services.common.FreeMarkerService;
import com.webutils.services.mail.template.MailTemplateConfigService;
import com.webutils.services.mail.template.MailTemplateEntity;
import com.webutils.services.mail.template.MailTemplateService;
import com.yukthitech.utils.ReflectionUtils;
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
	 * Mail template configuration service, used to fetch meta information from
	 * context.	 */
	@Autowired
	private MailTemplateConfigService mailTemplateConfigService;
	
	/**
	 * Default email server settings to be used.
	 */
	@Autowired(required = false)
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
	private Session newSession(EmailServerSettings settings, boolean forImap)
	{
		String key = settings.getSmtpHost() + ":" + settings.getSmtpPort() + "@" + settings.getUserName() + "$" + forImap;
		Session mailSession = sessionCache.get(key);
		
		if(mailSession != null)
		{
			return mailSession;
		}
		
		Properties configProperties = settings.toProperties(forImap);

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
	@SuppressWarnings("unchecked")
	private void addAttachments(Multipart multiPart, Object context, List<File> tempFiles) throws Exception
	{
		if(context == null)
		{
			return;
		}

		MailTemplateConfiguration mailTemplateConfiguration = mailTemplateConfigService.getMailTemplateConfiguration(context.getClass());

		if(mailTemplateConfiguration == null)
		{
			logger.warn("As specified context is not defined as mail template configuration, no attachments will be added. Context type: " + context.getClass().getName());
			return;
		}

		Set<MailTemplateConfiguration.Attachment> attachmentConfigs = mailTemplateConfiguration.getAttachments();

		if(attachmentConfigs == null || attachmentConfigs.isEmpty())
		{
			return;
		}

		FileDataSource fileSource = null;
		MimeBodyPart fileBodyPart = null;

		Object value = null;
		String attachmentName = null;

		for(MailTemplateConfiguration.Attachment attachment : attachmentConfigs)
		{
			value = ReflectionUtils.getNestedFieldValue(context, attachment.getField());

			if(value == null)
			{
				continue;
			}

			logger.debug("Adding attachment from field - " + attachment.getField());
			
			Collection<FileAttachment> fileAttachments = null;
			
			if(value instanceof File)
			{
				fileAttachments = Arrays.asList( new FileAttachment((File) value, attachment.getName()) );
			}
			else if(value instanceof Collection)
			{
				fileAttachments = (Collection<FileAttachment>) value;
			}
			else if(value instanceof FileAttachment)
			{
				fileAttachments = Arrays.asList((FileAttachment) value);
			}
			else if(value instanceof String)
			{
				File fieldFile = File.createTempFile(attachment.getName(), ".tmp");
				FileUtils.write(fieldFile, (String) value, Charset.defaultCharset());
				
				fileAttachments = Arrays.asList( new FileAttachment(fieldFile, attachment.getName()) );
				tempFiles.add(fieldFile);
			}
			else if(value instanceof byte[])
			{
				File fieldFile = File.createTempFile(attachment.getName(), ".tmp");
				FileUtils.writeByteArrayToFile(fieldFile, (byte[]) value);
				
				fileAttachments = Arrays.asList( new FileAttachment(fieldFile, attachment.getName()) );
				tempFiles.add(fieldFile);
			}
			else if(value instanceof Image)
			{
				attachmentName = attachment.getName();

				File fieldFile = File.createTempFile(attachment.getName(), ".tmp");
				String imgType = attachmentName.substring(attachmentName.lastIndexOf('.') + 1, attachmentName.length());

				if(!(value instanceof RenderableImage))
				{
					Image img = (Image) value;
					BufferedImage bimg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR);
					bimg.getGraphics().drawImage(img, 0, 0, null);

					value = bimg;
				}

				ImageIO.write((RenderedImage) value, imgType.toLowerCase(), fieldFile);
				
				fileAttachments = Arrays.asList( new FileAttachment(fieldFile, attachment.getName()) );
				tempFiles.add(fieldFile);
			}
			else
			{
				throw new InvalidStateException("Field {}.{} is marked as attachment with unsupported type", context.getClass().getName(), attachment.getField());
			}
			
			int index = 0;

			for(FileAttachment fileAttachment : fileAttachments)
			{
				index++;
				
				fileBodyPart = new MimeBodyPart();
				fileSource = new FileDataSource(fileAttachment.getFile());
	
				fileBodyPart.setDataHandler(new DataHandler(fileSource));
				fileBodyPart.setFileName(fileAttachment.getFileName());
				fileBodyPart.setHeader("Content-ID", "<" + attachment.getContentId() + "-" + index + ">");
	
				multiPart.addBodyPart(fileBodyPart);
			}
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
	private Message buildMessage(EmailServerSettings settings, MailTemplateEntity emailTemplate, Object context, List<File> tempFiles) throws AddressException, MessagingException
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
		Session mailSession = newSession(settings, false);

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
			addAttachments(multiPart, context, tempFiles);
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
	
	public void sendEmail(String templateName, Object context)
	{
		MailTemplateEntity mailTemplateEntity = mailTemplateService.fetchByName(templateName);
		
		if(mailTemplateEntity == null)
		{
			throw new InvalidArgumentException("Non existing mail template specified: {}", templateName);
		}
		
		sendEmail(emailServerSettings, mailTemplateEntity, context);
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
	public void sendEmail(EmailServerSettings settings, MailTemplateEntity email, Object context)
	{
		Message message = null;
		
		try
		{
			// Used to collect the temp files generated by this class alone (not externally sent)
			List<File> tempFiles = new ArrayList<>();
			
			// Build mail message object
			message = buildMessage(settings, email, context, tempFiles);
			
			// send the message
			Transport.send(message);
			
			// Delete temp files generated by this class (not externally attached files)
			tempFiles.forEach(file -> file.delete());
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
