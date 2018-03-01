package com.yukthitech.webutils.alerts;

import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.webutils.common.alerts.AlertDetails;

/**
 * Abstract class for all pull based alerting agents.
 * @author akiran
 */
public abstract class AbstractPullAlertingAgent implements IAlertingAgent
{
	/**
	 * Service to save alerts.
	 */
	@Autowired
	private PullAlertService pullAlertService;
	
	@Override
	public boolean sendAlert(AlertDetails alertDetails)
	{
		customize(alertDetails);
		
		Set<String> recipients = fetchRecipients(alertDetails);
		
		if(recipients == null || recipients.isEmpty())
		{
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
			
			alertWithTarget.setTarget(recipient);
			pullAlertService.save(alertDetails);
		}
				
		return true;
	}
	
	/**
	 * Can be overridden by child classes to customize alert details before sending.
	 * @param alertDetails details to be sent
	 */
	protected void customize(AlertDetails alertDetails)
	{}

	/**
	 * Child classes needs to fetch recipients based on alert being processed.
	 * @param alertDetails alert being sent
	 * @return recipients to which notification will be sent.
	 */
	protected abstract Set<String> fetchRecipients(AlertDetails alertDetails);
}
