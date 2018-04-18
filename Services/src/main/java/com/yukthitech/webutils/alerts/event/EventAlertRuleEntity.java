package com.yukthitech.webutils.alerts.event;

import javax.persistence.Column;
import javax.persistence.Table;

import com.yukthitech.webutils.repository.WebutilsEntity;

/**
 * Represents event alert rules based on which alerts can be sent when an event is occurred.
 */
@Table(name = "WEBUTILS_EVENT_ALERT_RULE")
public class EventAlertRuleEntity extends WebutilsEntity
{
	/**
	 * Name of the rule.
	 */
	@Column(name = "NAME", length = 100)
	private String name;
	
	/**
	 * Event type for which this rule should be evaluated.
	 */
	@Column(name = "EVENT_TYPE", length = 100)
	private String eventType;
	
	/**
	 * Free marker function body which should result in true or false. If not null, 
	 * this will be evaluated and if true, then only alert details will be evaluated and would be sent.
	 */
	@Column(name = "CONDITION_FUNC", length = 1000)
	private String conditionFunction;
	
	/**
	 * Alert details free marker template which should result in alert details json. 
	 * If condition is satisfied, the result alert would be sent.
	 */
	@Column(name = "ALERT_DETAILS_TEMPLATE", length = 3000)
	private String alertDetailsTemplate;

	/**
	 * Gets the event type for which this rule should be evaluated.
	 *
	 * @return the event type for which this rule should be evaluated
	 */
	public String getEventType()
	{
		return eventType;
	}

	/**
	 * Sets the event type for which this rule should be evaluated.
	 *
	 * @param eventType the new event type for which this rule should be evaluated
	 */
	public void setEventType(String eventType)
	{
		this.eventType = eventType;
	}

	/**
	 * Gets the free marker function body which should result in true or false. If not null, this will be evaluated and if true, then only alert details will be evaluated and would be sent.
	 *
	 * @return the free marker function body which should result in true or false
	 */
	public String getConditionFunction()
	{
		return conditionFunction;
	}

	/**
	 * Sets the free marker function body which should result in true or false. If not null, this will be evaluated and if true, then only alert details will be evaluated and would be sent.
	 *
	 * @param conditionFunction the new free marker function body which should result in true or false
	 */
	public void setConditionFunction(String conditionFunction)
	{
		this.conditionFunction = conditionFunction;
	}

	/**
	 * Gets the alert details free marker template which should result in alert details json. If condition is satisfied, the result alert would be sent.
	 *
	 * @return the alert details free marker template which should result in alert details json
	 */
	public String getAlertDetailsTemplate()
	{
		return alertDetailsTemplate;
	}

	/**
	 * Sets the alert details free marker template which should result in alert details json. If condition is satisfied, the result alert would be sent.
	 *
	 * @param alertDetailsTemplate the new alert details free marker template which should result in alert details json
	 */
	public void setAlertDetailsTemplate(String alertDetailsTemplate)
	{
		this.alertDetailsTemplate = alertDetailsTemplate;
	}

	/**
	 * Gets the name of the rule.
	 *
	 * @return the name of the rule
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the rule.
	 *
	 * @param name the new name of the rule
	 */
	public void setName(String name)
	{
		this.name = name;
	}
}
