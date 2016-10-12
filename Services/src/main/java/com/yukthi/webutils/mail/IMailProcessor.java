package com.yukthi.webutils.mail;

/**
 * Abstraction of mail processors.
 * @author akiran
 */
public interface IMailProcessor
{
	/**
	 * Called to process mails. 
	 * @param mailMessage Mail to be processed.
	 * @return true if mail has to be deleted.
	 */
	public boolean processAndDelete(MailMessage mailMessage);
}
