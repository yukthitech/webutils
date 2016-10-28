package com.yukthi.webutils.mail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents Mail message received. 
 * @author akiran
 */
public class ReceivedMailMessage
{
	/**
	 * Represent mail attachment in received mail.
	 * @author akiran
	 */
	public static class Attachment
	{
		/**
		 * Attachment content in temp file.
		 */
		private File file;
		
		/**
		 * Name of the attachment.
		 */
		private String name;
		
		/**
		 * Instantiates a new attachment.
		 */
		public Attachment()
		{}

		/**
		 * Instantiates a new attachment.
		 *
		 * @param file the file
		 * @param name the name
		 */
		public Attachment(File file, String name)
		{
			this.file = file;
			this.name = name;
		}

		/**
		 * Gets the attachment content in temp file.
		 *
		 * @return the attachment content in temp file
		 */
		public File getFile()
		{
			return file;
		}

		/**
		 * Sets the attachment content in temp file.
		 *
		 * @param file the new attachment content in temp file
		 */
		public void setFile(File file)
		{
			this.file = file;
		}

		/**
		 * Gets the name of the attachment.
		 *
		 * @return the name of the attachment
		 */
		public String getName()
		{
			return name;
		}

		/**
		 * Sets the name of the attachment.
		 *
		 * @param name the new name of the attachment
		 */
		public void setName(String name)
		{
			this.name = name;
		}
	}
	
	/**
	 * Message part of the mail.
	 * 
	 * @author akiran
	 */
	public static class MessagePart
	{
		/**
		 * Content of the message part.
		 */
		private String content;
		
		/**
		 * Headers of mail part.
		 */
		private Map<String, String> headers;

		/**
		 * Instantiates a new message part.
		 */
		public MessagePart()
		{}
		
		/**
		 * Instantiates a new message part.
		 *
		 * @param content the content
		 * @param headers the headers
		 */
		public MessagePart(String content, Map<String, String> headers)
		{
			this.content = content;
			this.headers = headers;
		}

		/**
		 * Gets the content of the message part.
		 *
		 * @return the content of the message part
		 */
		public String getContent()
		{
			return content;
		}

		/**
		 * Sets the content of the message part.
		 *
		 * @param content the new content of the message part
		 */
		public void setContent(String content)
		{
			this.content = content;
		}

		/**
		 * Gets the headers of mail part.
		 *
		 * @return the headers of mail part
		 */
		public Map<String, String> getHeaders()
		{
			return headers;
		}

		/**
		 * Sets the headers of mail part.
		 *
		 * @param headers the new headers of mail part
		 */
		public void setHeaders(Map<String, String> headers)
		{
			this.headers = headers;
		}
	}
	
	/**
	 * Mail id from which mail is received.
	 */
	private String fromMailId;
	
	/**
	 * Mail message subject.
	 */
	private String subject;
	
	/**
	 * Attachments received in mail.
	 */
	private List<Attachment> attachments;
	
	/**
	 * Parts of the mail message with headers.
	 */
	private List<MessagePart> messageParts;
	
	/**
	 * Instantiates a new mail message.
	 */
	public ReceivedMailMessage()
	{}

	/**
	 * Instantiates a new mail message.
	 *
	 * @param fromMailId the from mail id
	 * @param subject the subject
	 */
	public ReceivedMailMessage(String fromMailId, String subject)
	{
		this.fromMailId = fromMailId;
		this.subject = subject;
	}

	/**
	 * Gets the mail id from which mail is received.
	 *
	 * @return the mail id from which mail is received
	 */
	public String getFromMailId()
	{
		return fromMailId;
	}

	/**
	 * Sets the mail id from which mail is received.
	 *
	 * @param fromMailId the new mail id from which mail is received
	 */
	public void setFromMailId(String fromMailId)
	{
		this.fromMailId = fromMailId;
	}

	/**
	 * Gets the mail message subject.
	 *
	 * @return the mail message subject
	 */
	public String getSubject()
	{
		return subject;
	}

	/**
	 * Sets the mail message subject.
	 *
	 * @param subject the new mail message subject
	 */
	public void setSubject(String subject)
	{
		this.subject = subject;
	}

	/**
	 * Gets the attachments received in mail.
	 *
	 * @return the attachments received in mail
	 */
	public List<Attachment> getAttachments()
	{
		return attachments;
	}

	/**
	 * Sets the attachments received in mail.
	 *
	 * @param attachments the new attachments received in mail
	 */
	public void setAttachments(List<Attachment> attachments)
	{
		this.attachments = attachments;
	}
	
	/**
	 * Adds specified attachment to this mail.
	 * @param attachment Attachment to be added.
	 */
	public void addAttachment(Attachment attachment)
	{
		if(this.attachments == null)
		{
			this.attachments = new ArrayList<>();
		}
		
		this.attachments.add(attachment);
	}

	/**
	 * Gets the parts of the mail message with headers.
	 *
	 * @return the parts of the mail message with headers
	 */
	public List<MessagePart> getMessageParts()
	{
		return messageParts;
	}

	/**
	 * Sets the parts of the mail message with headers.
	 *
	 * @param messageParts the new parts of the mail message with headers
	 */
	public void setMessageParts(List<MessagePart> messageParts)
	{
		this.messageParts = messageParts;
	}

	/**
	 * Adds specified part to this message.
	 * @param part Part to be added.
	 */
	public void addMessagePart(MessagePart part)
	{
		if(this.messageParts == null)
		{
			this.messageParts = new ArrayList<>();
		}
		
		this.messageParts.add(part);
	}
}
