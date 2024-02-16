package com.yukthitech.webutils.mail;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.imageio.ImageIO;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yukthitech.utils.ObjectWrapper;
import com.yukthitech.utils.ReflectionUtils;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.webutils.common.mail.IMailCustomizer;
import com.yukthitech.webutils.common.mailtemplate.MailTemplateConfiguration;
import com.yukthitech.webutils.common.models.mails.EmailServerSettings;
import com.yukthitech.webutils.common.models.mails.MailReadProtocol;
import com.yukthitech.webutils.mail.template.MailTemplateConfigService;
import com.yukthitech.webutils.mail.template.MailTemplateEntity;
import com.yukthitech.webutils.services.freemarker.FreeMarkerService;

/**
 * Service to send and receive mails.
 * 
 * @author akiran
 */
@Service
public class EmailService
{
	private static Logger logger = LogManager.getLogger(EmailService.class);
	
	/**
	 * Time format to be used in mails.
	 */
	private static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("dd MMM yyyy hh:mm:ss aa");

	/**
	 * Mail template configuration service, used to fetch meta information from
	 * context.
	 */
	@Autowired
	private MailTemplateConfigService mailTemplateConfigService;

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
				throw new InvalidArgumentException(ex, "An error occurred while parsing email-id {} from {} list", ids[i], listName);
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
	private void addAttachments(Multipart multiPart, Object context) throws Exception
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
				FileUtils.write(fieldFile, (String) value);
				
				fileAttachments = Arrays.asList( new FileAttachment(fieldFile, attachment.getName()) );
			}
			else if(value instanceof byte[])
			{
				File fieldFile = File.createTempFile(attachment.getName(), ".tmp");
				FileUtils.writeByteArrayToFile(fieldFile, (byte[]) value);
				
				fileAttachments = Arrays.asList( new FileAttachment(fieldFile, attachment.getName()) );
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
	private Message buildMessage(EmailServerSettings settings, MailTemplateEntity emailTemplate, Object context) throws AddressException, MessagingException
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
			addAttachments(multiPart, context);
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
			// Build mail message object
			message = buildMessage(settings, email, context);
			
			// send the message
			Transport.send(message);
		} catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while sending email - {}", email, ex);
		}
		
		try
		{
			copyToSentFolder(settings, message);
		} catch(Exception ex)
		{
			logger.debug("An error occurred while copying mail to sent folder.", ex);
		}
	}

	public File generateEmlFile(EmailServerSettings settings, MailTemplateEntity email, Object context)
	{
		Message message = null;
		
		try
		{
			// Build mail message object
			message = buildMessage(settings, email, context);
			
			File emlFile = File.createTempFile("sample", ".eml");
			FileOutputStream fos = new FileOutputStream(emlFile);
			
			message.writeTo(fos);
			fos.flush();
			fos.close();
			
			return emlFile;
		} catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while generating eml file - {}", email, ex);
		}
	}

	/**
	 * Extracts mail content into specified mail message.
	 * 
	 * @param mailMessage
	 *            Mail message to which content needs to be fetched.
	 * @param content
	 *            Content to be parsed into mail message.
	 * @param contentType
	 *            Content type.
	 */
	private void extractMailContent(ReceivedMailMessage mailMessage, Object content, String contentType) throws MessagingException, IOException
	{
		if(!contentType.toLowerCase().contains("multipart"))
		{
			mailMessage.addTextContent(content.toString());
			return;
		}

		Multipart multipart = (Multipart) content;
		int count = multipart.getCount();
		BodyPart part = null;
		File attachmentFile = null;

		for(int i = 0; i < count; i++)
		{
			part = multipart.getBodyPart(i);

			if(Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition()))
			{
				attachmentFile = File.createTempFile(part.getFileName(), ".attachment");
				((MimeBodyPart) part).saveFile(attachmentFile);

				mailMessage.addAttachment(new ReceivedMailMessage.Attachment(attachmentFile, part.getFileName()));
			}
			else if(part.getContentType().toLowerCase().contains("text/html") && !mailMessage.hasContent())
			{
				String contentStr = IOUtils.toString(part.getInputStream());
				mailMessage.setContent(contentStr);
			}
			else if(part.getContentType().toLowerCase().contains("text"))
			{
				String contentStr = IOUtils.toString(part.getInputStream());
				mailMessage.addTextContent(contentStr);
			}
		}
	}
	
	/**
	 * Used to recipients list during "Reply" action for specified message.
	 * @param recipientType Recipient type 
	 * @param currentMailId Current mail id where mail was received
	 * @param message Message for which reply needs to be sent.
	 * @param replyMessage  Reply message being formed
	 * @return true if atleast one addess is added.
	 */
	private boolean setRecipientsForReply(Message.RecipientType recipientType, String currentMailId, Message message, Message replyMessage) throws MessagingException
	{
		Address addresses[] = message.getRecipients(recipientType);
		
		if(addresses == null || addresses.length == 0)
		{
			addresses = new Address[0];
		}
		
		List<String> finalLst = new ArrayList<>();
		
		for(Address address : addresses)
		{
			if(address.toString().equals(currentMailId))
			{
				continue;
			}
			
			finalLst.add(address.toString());
		}
		
		if(recipientType == Message.RecipientType.TO)
		{
			addresses = message.getFrom();
			
			for(Address address : addresses)
			{
				if(address.toString().equals(currentMailId))
				{
					continue;
				}
				
				finalLst.add(address.toString());
			}
		}
		
		if(finalLst.isEmpty())
		{
			return false;
		}
		
		replyMessage.setRecipients(recipientType, convertToInternetAddress(recipientType.toString(), finalLst.toArray(new String[0])));
		return true;
	}
	
	/**
	 * Replies to specified mail with specified content.
	 * @param session Session to be used.
	 * @param message Message to be replied.
	 * @param emailTemplate Email template to build content.
	 * @param context Context for processing template.
	 * @param replyToAll True, if all participants has to be included in reply.
 	 * @param currentMailId Current mail id whose folder is being read
	 * @throws MailProcessingException Thrown when an error occurs while sending mail.
	 */
	private void replyToMail(Session session, Message message, MailTemplateEntity emailTemplate, Object context, boolean replyToAll, String currentMailId) throws MailProcessingException
	{
		String subject = null;
		
		try
		{
			subject = message.getSubject();
			
			MimeMessage replyMessage = (MimeMessage) message.reply(false);
			boolean foundMailLst = false;
			
			if(setRecipientsForReply(Message.RecipientType.TO, currentMailId, message, replyMessage))
			{
				foundMailLst = true;
			}
			
			if(setRecipientsForReply(Message.RecipientType.CC, currentMailId, message, replyMessage))
			{
				foundMailLst = true;
			}
			
			if(!foundMailLst)
			{
				logger.warn("Failed to send reply to mail with subject - {}. As it is self sent mail", message.getSubject());
				return;
			}

			//create and set content of reply mail
			String content = freeMarkerService.processTemplate(emailTemplate.getTemplateName() + ".content", emailTemplate.getContentTemplate(), context);
			
			// create multi part message
			Multipart multiPart = new MimeMultipart();
	
			// add body to multi part
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(content, "text/html");
			multiPart.addBodyPart(messageBodyPart);
			
			//add header part before adding original content
			StringBuilder headerPart = new StringBuilder("<BR/><BR/>---------------------  Original Mail Content  ----------------------------------<BR/><BR/>");
			headerPart.append("<table>");
			headerPart.append("<tr><td><B>From: </B></td><td>").append(Arrays.toString(message.getFrom())).append("</td></tr>");
			
			if(message.getRecipients(Message.RecipientType.TO) != null)
			{
				headerPart.append("<tr><td><B>TO: </B></td><td>").append(Arrays.toString(message.getRecipients(Message.RecipientType.TO))).append("</td></tr>");
			}
			
			if(message.getRecipients(Message.RecipientType.CC) != null)
			{
				headerPart.append("<tr><td><B>CC: </B></td><td>").append(Arrays.toString(message.getRecipients(Message.RecipientType.CC))).append("</td></tr>");
			}
			
			headerPart.append("<tr><td><B>Received On: </B></td><td>").append( TIME_FORMAT.format(message.getReceivedDate()) ).append("</td></tr>");
			
			headerPart.append("</table><BR/>");

			BodyPart headerContent = new MimeBodyPart();
			headerContent.setContent(headerPart.toString(), "text/html");
			multiPart.addBodyPart(headerContent);

			//add original mail content
			BodyPart actualContent = new MimeBodyPart();
			actualContent.setDataHandler(message.getDataHandler());
			multiPart.addBodyPart(actualContent);
			
			replyMessage.setContent(multiPart);
			
			Transport.send(replyMessage);
		}catch(Exception ex)
		{
			throw new MailProcessingException(ex, "An error occured while replying to specified mail with subject - {}", subject);
		}
	}

	/**
	 * Reads mails from specified folder and for each mail, mail-processor will
	 * be invoked.
	 * 
	 * @param session Mail session to be used.
	 * @param store
	 *            Mails store to check.
	 * @param folderName
	 *            Folder name to check.
	 * @param mailProcessor
	 *            Processor for processing mails.
	 * @param currentMailId Current mail id whose folder is being read
	 */
	private void readMailsFromFolder(Session session, Store store, String folderName, IMailProcessor mailProcessor, String currentMailId) throws MessagingException, IOException
	{
		Folder mailFolder = store.getFolder(folderName);
		mailFolder.open(Folder.READ_WRITE);

		Message[] messages = mailFolder.getMessages();
		IMailProcessingContext context = null;
		ObjectWrapper<Boolean> deleteFlag = new ObjectWrapper<>();

		for(int i = 0; i < messages.length; i++)
		{
			Message message = messages[i];
			String subject = message.getSubject();

			String nameMailId = message.getFrom()[0].toString();
			String frmMailId = nameMailId;
			
			if(nameMailId.contains("<"))
			{
				frmMailId = nameMailId.substring(nameMailId.indexOf("<") + 1, nameMailId.indexOf(">")).trim();
			}

			ReceivedMailMessage mailMessage = new ReceivedMailMessage(frmMailId, subject);
			extractMailContent(mailMessage, message.getContent(), message.getContentType());

			// buld the current mail context
			deleteFlag.setValue(false);

			context = new IMailProcessingContext()
			{
				@Override
				public void reply(MailTemplateEntity emailTemplate, Object context) throws MailProcessingException
				{
					replyToMail(session, message, emailTemplate, context, false, currentMailId);
				}

				@Override
				public void delete()
				{
					deleteFlag.setValue(true);
				}

				@Override
				public void replyToAll(MailTemplateEntity emailTemplate, Object context) throws MailProcessingException
				{
					replyToMail(session, message, emailTemplate, context, true, currentMailId);
				}
			};

			// process the mail
			mailProcessor.process(context, mailMessage);

			// if mail is marked for delete
			if(deleteFlag.getValue())
			{
				message.setFlag(Flags.Flag.DELETED, true);
			}
		}

		mailFolder.close(false);
	}

	/**
	 * Reads the mails from the email server specified by settings.
	 * 
	 * @param settings
	 *            Mail server settings from which mails has to be read.
	 * @param mailProcessor
	 *            Processor to process read mails.
	 */
	public void readMails(EmailServerSettings settings, IMailProcessor mailProcessor)
	{
		try
		{
			Session session = newSession(settings, true);

			Store store = session.getStore(MailReadProtocol.IMAPS.getName());
			store.connect(settings.getImapHost(), settings.getUserName(), settings.getPassword());

			for(String folderName : settings.getFolderNames())
			{
				readMailsFromFolder(session, store, folderName, mailProcessor, settings.getUserName());
			}

			store.close();
		} catch(Exception e)
		{
			throw new IllegalStateException("Exception occured while reading the mail ", e);
		}
	}
}
