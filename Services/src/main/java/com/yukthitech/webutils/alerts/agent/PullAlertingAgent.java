package com.yukthitech.webutils.alerts.agent;

import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.webutils.alerts.PullAlertService;
import com.yukthitech.webutils.common.alerts.AlertDetails;
import com.yukthitech.webutils.common.alerts.AlertingAgentType;
import com.yukthitech.webutils.common.alerts.PullAlertStatus;

/**
 * Abstract class for all pull based alerting agents.
 * @author akiran
 */
@Service
public class PullAlertingAgent implements IAlertingAgent
{
	private static Logger logger = LogManager.getLogger(PullAlertingAgent.class);
	
	/**
	 * Service to save alerts.
	 */
	@Autowired
	private PullAlertService pullAlertService;
	
	/**
	 * Alert support provided by applications.
	 */
	@Autowired
	private IAlertSupport alertSupport;
	
	@Override
	public AlertingAgentType getType()
	{
		return AlertingAgentType.PULL_ALERTING_AGENT;
	}

	@Override
	public boolean sendAlert(AlertDetails alertDetails)
	{
		Set<String> recipients = alertSupport.fetchPullRecipients(alertDetails);
		
		if(recipients == null || recipients.isEmpty())
		{
			logger.debug("No recipient found based on target '{}' and alert-type '{}' for alert with title: {}", alertDetails.getTarget(), alertDetails.getAlertType(), alertDetails.getTitle());
			return false;
		}
		
		AlertDetails alertWithTarget = null;

		for(String recipient : recipients)
		{
			alertWithTarget = new AlertDetails();
			
			try
			{
				BeanUtils.copyProperties(alertWithTarget, alertDetails);
			}catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while copying alert properties", ex);
			}
			
			alertWithTarget.setStatus(PullAlertStatus.NOT_PROCESSED);
			alertWithTarget.setTarget(recipient);
			
			pullAlertService.saveOrUpdate(alertWithTarget);
		}
				
		return true;
	}
}
