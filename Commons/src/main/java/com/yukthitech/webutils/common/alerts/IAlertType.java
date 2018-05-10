package com.yukthitech.webutils.common.alerts;

import java.util.Set;

/**
 * Abstraction of alert types.
 * @author akiran
 */
public interface IAlertType
{
	/**
	 * Agent types to be used for this alert type.
	 * @return agent types to be used.
	 */
	public Set<AlertingAgentType> getAlertingAgentTypes();
}
