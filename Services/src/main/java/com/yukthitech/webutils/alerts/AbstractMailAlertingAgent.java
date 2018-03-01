package com.yukthitech.webutils.alerts;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.webutils.common.alerts.AlertDetails;
import com.yukthitech.webutils.common.models.mails.EmailServerSettings;
import com.yukthitech.webutils.mail.EmailService;
import com.yukthitech.webutils.mail.template.MailTemplateEntity;

/**
 * Abstract class alerting agent which send alerts by mail.
 * @author akiran
 */
public abstract class AbstractMailAlertingAgent implements IAlertingAgent
{
	/**
	 * Used to send mails.
	 */
	@Autowired
	private EmailService emailService;
	
	@Override
	public boolean sendAlert(AlertDetails alertDetails)
	{
		customize(alertDetails);
		
		Set<String> recipients = fetchRecipients(alertDetails);
		
		if(recipients == null || recipients.isEmpty())
		{
			return false;
		}
		
		EmailServerSettings emailServerSettings = getEmailServerSettings();
		
		MailTemplateEntity mailTemplateEntity = new MailTemplateEntity();
		mailTemplateEntity.setToListTemplate( recipients.stream().collect(Collectors.joining(",")) );
		mailTemplateEntity.setSubjectTemplate(alertDetails.getMessage());
		mailTemplateEntity.setContentTemplate(alertDetails.getMessage());
		
		emailService.sendEmail(emailServerSettings, mailTemplateEntity, Collections.emptyMap());
		return true;
	}
	
	/**
	 * Used to fetch email server settings which would be used to send
	 * alerts by mail.
	 * @return mail server settings
	 */
	protected abstract EmailServerSettings getEmailServerSettings();
	
	/**
	 * Can be overridden by child classes to customize alert details before sending.
	 * @param alertDetails details to be sent
	 */
	protected void customize(AlertDetails alertDetails)
	{}
	
	/**
	 * Child classes needs to fetch recipients based on alert being processed.
	 * @param alertDetails alert being sent
	 * @return recipient mail ids to which notification will be sent.
	 */
	protected abstract Set<String> fetchRecipients(AlertDetails alertDetails);
}
