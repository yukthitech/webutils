package com.yukthitech.webutils.alerts.agent;

import java.util.Set;

import com.yukthitech.webutils.common.alerts.AlertDetails;

/**
 * Abstraction of alerting agent which would be responsible of sending alerts on 
 * different means - mails, mobiles, erc.
 * @author akiran
 */
public interface IAlertingAgent
{
	/**
	 * Checks whether current agent is compatible with specified types.
	 * @param targetTypes target types specified by alert.
	 * @return true if current agent is compatible with target type.
	 */
	public boolean isCompatible(Set<String> targetTypes);
	
	/**
	 * Process and send the alert to appropriate members. Based on different factors alerting
	 * agent may decide to ignore alert and in such cases it should return false.
	 * 
	 * @param alertDetails alert to send
	 * @return true if alert is processed by this agent.
	 */
	public boolean sendAlert(AlertDetails alertDetails);
}
