package com.yukthitech.webutils.alerts;

import com.yukthitech.webutils.common.alerts.AlertDetails;

/**
 * Abstraction of alerting agent which would be responsible of sending alerts on 
 * different means - mails, mobiles, erc.
 * @author akiran
 */
public interface IAlertingAgent
{
	/**
	 * Process and send the alert to appropriate members. Based on different factors alerting
	 * agent may decide to ignore alert and in such cases it should return false.
	 * 
	 * @param alertDetails alert to send
	 * @return true if alert is processed by this agent.
	 */
	public boolean sendAlert(AlertDetails alertDetails);
}
