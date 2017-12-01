package com.yukthitech.webutils.mail;

import com.yukthitech.webutils.mail.template.MailTemplateEntity;

/**
 * Context object sent during processing mails.
 * @author akiran
 */
public interface IMailProcessingContext
{
	/**
	 * Sends reply to current mail using specified template and context.
	 * @param emailTemplate Template to be used for reply
	 * @param context Context for template processing.
	 */
	public void reply(MailTemplateEntity emailTemplate, Object context) throws MailProcessingException;
	
	/**
	 * Sends reply to (all involved members of) current mail using specified template and context.
	 * @param emailTemplate Template to be used for reply
	 * @param context Context for template processing.
	 */
	public void replyToAll(MailTemplateEntity emailTemplate, Object context) throws MailProcessingException;
	
	/**
	 * Marks the mail for deleting, which would happen at end of processing the mail.
	 */
	public void delete();
}
