package com.yukthitech.webutils.alerts.agent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.webutils.alerts.PullAlertService;
import com.yukthitech.webutils.common.alerts.AlertDetails;
import com.yukthitech.webutils.common.alerts.PullAlertStatus;

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

	/**
	 * Indicates type of agent.
	 */
	private Set<String> types;
	
	/**
	 * Instantiates a new abstract mail alerting agent.
	 */
	public AbstractPullAlertingAgent()
	{
		this.types = new HashSet<>();
	}
	
	/**
	 * Instantiates a new abstract mail alerting agent.
	 *
	 * @param types types of agent
	 */
	public AbstractPullAlertingAgent(Object... types)
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
			
			pullAlertService.save(alertWithTarget);
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
