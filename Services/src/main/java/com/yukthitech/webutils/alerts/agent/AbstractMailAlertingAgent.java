package com.yukthitech.webutils.alerts.agent;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
	
	/**
	 * Indicates type of agent.
	 */
	private Set<String> types;
	
	/**
	 * Instantiates a new abstract mail alerting agent.
	 */
	public AbstractMailAlertingAgent()
	{
		this.types = new HashSet<>();
	}
	
	/**
	 * Instantiates a new abstract mail alerting agent.
	 *
	 * @param types types of agent
	 */
	public AbstractMailAlertingAgent(Object... types)
	{
		List<String> typesAsStr = Arrays.asList(types)
				.stream()
				.map(type -> type.toString())
				.collect(Collectors.toList());
		
		this.types = new HashSet<>(typesAsStr);
	}

	@Override
	public boolean isCompatible(Set<String> targetTypes)
	{
		return CollectionUtils.containsAny(types, targetTypes);
	}

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
