package com.yukthitech.webutils.alerts.agent;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yukthitech.webutils.common.alerts.AlertDetails;
import com.yukthitech.webutils.common.alerts.AlertingAgentType;
import com.yukthitech.webutils.common.models.mails.EmailServerSettings;
import com.yukthitech.webutils.mail.EmailService;
import com.yukthitech.webutils.mail.template.MailTemplateEntity;

/**
 * Abstract class alerting agent which send alerts by mail.
 * @author akiran
 */
@Service
public class MailAlertingAgent implements IAlertingAgent
{
	/**
	 * Used to send mails.
	 */
	@Autowired
	private EmailService emailService;
	
	/**
	 * Email server settings to send mail.
	 */
	@Autowired
	private EmailServerSettings emailServerSettings;
	
	/**
	 * Alert support provided by applications.
	 */
	@Autowired(required = false)
	private IAlertSupport alertSupport;
	
	@Override
	public AlertingAgentType getType()
	{
		return AlertingAgentType.MAIL_ALERTING_AGENT;
	}

	@Override
	public boolean sendAlert(AlertDetails alertDetails)
	{
		Set<String> recipients = alertSupport.fetchMailRecipients(alertDetails);
		
		if(recipients == null || recipients.isEmpty())
		{
			return false;
		}
		
		MailTemplateEntity mailTemplateEntity = new MailTemplateEntity();
		mailTemplateEntity.setToListTemplate( recipients.stream().collect(Collectors.joining(",")) );
		mailTemplateEntity.setSubjectTemplate(alertDetails.getTitle());
		
		String message = null;
		
		//if long message is specified, use it for mails
		if(StringUtils.isNotBlank(alertDetails.getLongMessage()))
		{
			message = alertDetails.getLongMessage();
		}
		else
		{
			message = alertDetails.getMessage();
		}
		
		if(message != null)
		{
			message = message.replace("\n", "\n<br/>");
			message = message.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
		}
		
		mailTemplateEntity.setContentTemplate(message);
		
		emailService.sendEmail(emailServerSettings, mailTemplateEntity, null);
		return true;
	}
}
