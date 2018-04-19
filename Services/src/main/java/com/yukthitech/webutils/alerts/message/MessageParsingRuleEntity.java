package com.yukthitech.webutils.alerts.message;

import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Table;

import com.yukthitech.persistence.annotations.DataType;
import com.yukthitech.persistence.annotations.DataTypeMapping;
import com.yukthitech.persistence.conversion.impl.JsonWithTypeConverter;
import com.yukthitech.webutils.repository.WebutilsEntity;

/**
 * Indicates rule which is used to convert message (sms or mail) into an alert.
 * @author akiran
 */
@Table(name = "WEBUTILS_ALERT_MSSG_PARSING_RULE")
public class MessageParsingRuleEntity extends WebutilsEntity
{
	/**
	 * Agent of user with this role only
	 * will be used to use this rule.
	 */
	@Column(name = "USER_ROLE", length = 100)
	@DataTypeMapping(converterType = JsonWithTypeConverter.class, type = DataType.STRING)
	private Object userRole;
	
	/**
	 * From address pattern to be used.
	 */
	@Column(name = "FROM_ADDR_PTRN", length = 500)
	private String fromAddressPattern;
	
	/**
	 * Filter pattern only when matched, the target message will be processed to alert.
	 */
	@Column(name = "MSG_FILTER_PTRN", length = 1000)
	private String messageFilterPattern;
	
	/**
	 * This pattern will be searched with in pattern. And the named groups found in the message
	 * will be used to construct alert bean, if specified.
	 */
	@Column(name = "MSG_PTRNS", length = 2000)
	@DataTypeMapping(converterType = JsonWithTypeConverter.class, type = DataType.STRING)
	private Set<String> messagePatterns;
	
	/**
	 * Alert event type to be executed when this message parsing rule is
	 * matched.
	 */
	@Column(name = "ALERT_EVENT_TYPE", length = 100)
	private String alertEventType;
	
	/**
	 * Default attributes to be set when this rule is matched.
	 */
	@Column(name = "DEF_ATTR", length = 2000)
	@DataTypeMapping(converterType = JsonWithTypeConverter.class, type = DataType.STRING)
	private Map<String, Object> defaultAttributes;

	/**
	 * Gets the agent of user with this role only will be used to use this rule.
	 *
	 * @return the agent of user with this role only will be used to use this rule
	 */
	public Object getUserRole()
	{
		return userRole;
	}

	/**
	 * Sets the agent of user with this role only will be used to use this rule.
	 *
	 * @param userRole the new agent of user with this role only will be used to use this rule
	 */
	public void setUserRole(Object userRole)
	{
		this.userRole = userRole;
	}

	/**
	 * Gets the from address pattern to be used.
	 *
	 * @return the from address pattern to be used
	 */
	public String getFromAddressPattern()
	{
		return fromAddressPattern;
	}

	/**
	 * Sets the from address pattern to be used.
	 *
	 * @param fromAddressPattern the new from address pattern to be used
	 */
	public void setFromAddressPattern(String fromAddressPattern)
	{
		this.fromAddressPattern = fromAddressPattern;
	}

	/**
	 * Gets the filter pattern only when matched, the target message will be processed to alert.
	 *
	 * @return the filter pattern only when matched, the target message will be processed to alert
	 */
	public String getMessageFilterPattern()
	{
		return messageFilterPattern;
	}

	/**
	 * Sets the filter pattern only when matched, the target message will be processed to alert.
	 *
	 * @param messageFilterPattern the new filter pattern only when matched, the target message will be processed to alert
	 */
	public void setMessageFilterPattern(String messageFilterPattern)
	{
		this.messageFilterPattern = messageFilterPattern;
	}

	/**
	 * Gets the this pattern will be searched with in pattern. And the named groups found in the message will be used to construct alert bean, if specified.
	 *
	 * @return the this pattern will be searched with in pattern
	 */
	public Set<String> getMessagePatterns()
	{
		return messagePatterns;
	}

	/**
	 * Sets the this pattern will be searched with in pattern. And the named groups found in the message will be used to construct alert bean, if specified.
	 *
	 * @param messagePatterns the new this pattern will be searched with in pattern
	 */
	public void setMessagePatterns(Set<String> messagePatterns)
	{
		this.messagePatterns = messagePatterns;
	}

	/**
	 * Gets the alert event type to be executed when this message parsing rule is matched.
	 *
	 * @return the alert event type to be executed when this message parsing rule is matched
	 */
	public String getAlertEventType()
	{
		return alertEventType;
	}

	/**
	 * Sets the alert event type to be executed when this message parsing rule is matched.
	 *
	 * @param alertEventType the new alert event type to be executed when this message parsing rule is matched
	 */
	public void setAlertEventType(String alertEventType)
	{
		this.alertEventType = alertEventType;
	}

	/**
	 * Gets the default attributes to be set when this rule is matched.
	 *
	 * @return the default attributes to be set when this rule is matched
	 */
	public Map<String, Object> getDefaultAttributes()
	{
		return defaultAttributes;
	}

	/**
	 * Sets the default attributes to be set when this rule is matched.
	 *
	 * @param defaultAttributes the new default attributes to be set when this rule is matched
	 */
	public void setDefaultAttributes(Map<String, Object> defaultAttributes)
	{
		this.defaultAttributes = defaultAttributes;
	}
}
