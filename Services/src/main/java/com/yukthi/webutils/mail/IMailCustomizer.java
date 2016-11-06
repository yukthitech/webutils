package com.yukthi.webutils.mail;

/**
 * Interfaces to be implemented by mail template context classes which want to customize
 * mail to be sent.
 * 
 * @author akiran
 */
public interface IMailCustomizer
{
	/**
	 * Implementation can customize the provided mail message before sending. 
	 * @param mailMessage Mail message being sent.
	 * @param templateCustomization Customization data set on mail template.
	 */
	public void customize(MailMessage mailMessage, Object templateCustomization);
}
