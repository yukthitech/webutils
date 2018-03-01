package com.yukthitech.webutils.alerts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.yukthitech.webutils.common.alerts.AlertDetails;

/**
 * Service to register alerts and push alerts to agents.
 * @author akiran
 */
@Service
public class AlertEngine
{
	/**
	 * Spring context for fetching agents.
	 */
	@Autowired
	private ApplicationContext applicationContext;
	
	/**
	 * List of registered alerting agents.
	 */
	private List<IAlertingAgent> alertingAgents = new ArrayList<>();
	
	/**
	 * Post construct method to fetch agents.
	 */
	@PostConstruct
	private void init()
	{
		Map<String, IAlertingAgent> agents = applicationContext.getBeansOfType(IAlertingAgent.class);
		
		if(agents == null || agents.isEmpty())
		{
			return;
		}
		
		for(IAlertingAgent agent : agents.values())
		{
			this.alertingAgents.add(agent);
		}
	}
	
	/**
	 * Sends alerts to appropriated agents.
	 * @param alertDetails alert to be sent.
	 */
	public void sendAlert(AlertDetails alertDetails)
	{
		for(IAlertingAgent agent : alertingAgents)
		{
			agent.sendAlert(alertDetails);
		}
	}
}
