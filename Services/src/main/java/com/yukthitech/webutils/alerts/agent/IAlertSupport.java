package com.yukthitech.webutils.alerts.agent;

import java.util.Set;

import com.yukthitech.webutils.common.alerts.AlertDetails;
import com.yukthitech.webutils.common.alerts.IAlertType;

/**
 * Interface that application are expected to implement so support alerting agents. 
 * @author akiran
 */
public interface IAlertSupport
{
	/**
	 * Child classes needs to fetch recipients based on alert being processed.
	 * @param alertDetails alert being sent
	 * @return recipient mail ids to which notification will be sent.
	 */
	public Set<String> fetchMailRecipients(AlertDetails alertDetails);

	/**
	 * Child classes needs to fetch recipients based on alert being processed.
	 * @param alertDetails alert being sent
	 * @return recipients to which notification will be sent.
	 */
	public Set<String> fetchPullRecipients(AlertDetails alertDetails);
	
	/**
	 * Returns the alert type to be used to communicate unhandled errors.
	 * @return alert type.
	 */
	public IAlertType getErrorAlertType();
	
	/**
	 * Returns the alert type to be used to communicate system messages.
	 * @return alert type.
	 */
	public IAlertType getSystemAlertType();
	
	/**
	 * Returns the alert type to be used to communicate confirmation alerts.
	 * @return alert type.
	 */
	public IAlertType getConfirmationAlertType();
	
	/**
	 * Name of the agent to be used for system alerts.
	 * @return system agent name.
	 */
	public String getSystemAgentName();
}
