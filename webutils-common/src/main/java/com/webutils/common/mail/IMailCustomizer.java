package com.webutils.common.mail;

/**
 * Interfaces to be implemented by mail template context classes which want to customize
 * mail to be sent.
 * 
 * @author akiran
 */
public interface IMailCustomizer
{
	/**
	 * If custom from id to be used when sending the mail.
	 * @return from mail id to be used
	 */
	public String getFromId();
}
