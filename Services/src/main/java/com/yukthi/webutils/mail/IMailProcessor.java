package com.yukthi.webutils.mail;

/**
 * Abstraction of mail processors.
 * @author akiran
 */
public interface IMailProcessor
{
	/**
	 * Called to process mails. 
	 * @param context Context for mail processing.
	 * @param mailMessage Mail to be processed.
	 */
	public void process(IMailProcessingContext context, ReceivedMailMessage mailMessage);
}
