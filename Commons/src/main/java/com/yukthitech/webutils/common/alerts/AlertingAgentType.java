package com.yukthitech.webutils.common.alerts;

/**
 * Enumeration of alerting agent types.
 * @author akiran
 */
public enum AlertingAgentType
{
	/**
	 * Agent which can send mails.
	 */
	MAIL_ALERTING_AGENT,
	
	/**
	 * Alerts which can be pulled appropriated agents.
	 */
	PULL_ALERTING_AGENT,
	
	/**
	 * Alerts handled by system.
	 */
	SYSTEM_ALERT;
}
