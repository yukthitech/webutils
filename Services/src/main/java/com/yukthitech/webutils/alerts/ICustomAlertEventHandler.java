package com.yukthitech.webutils.alerts;

import com.yukthitech.webutils.alerts.event.EventAlertRuleEntity;

/**
 * Custom alert handler, if present in context when an event rule is matched and 
 * with custom data is found.
 * @author akiran
 */
public interface ICustomAlertEventHandler
{
	/**
	 * Fetches custom data type for specified rule.
	 * @param rule rule with custom data for which custom type needs to be fetched.
	 * @return custom data type.
	 */
	public Class<?> getCustomDataType(EventAlertRuleEntity rule);
	
	/**
	 * Invoked to invoke custom logic for the matched rule with specified custom data.
	 * @param rule rule to be processed
	 * @param customData custom data present with rule.
	 */
	public void handleCustomRule(EventAlertRuleEntity rule, Object customData);
}
