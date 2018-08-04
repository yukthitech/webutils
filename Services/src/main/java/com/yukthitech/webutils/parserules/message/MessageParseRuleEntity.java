package com.yukthitech.webutils.parserules.message;

import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Table;

import com.yukthitech.persistence.annotations.DataType;
import com.yukthitech.persistence.annotations.DataTypeMapping;
import com.yukthitech.persistence.annotations.UniqueConstraint;
import com.yukthitech.persistence.conversion.impl.JsonWithTypeConverter;
import com.yukthitech.webutils.repository.WebutilsEntity;

/**
 * Indicates rule which is used to convert message (sms or mail) into an alert.
 * @author akiran
 */
@Table(name = "WEBUTILS_MSSG_PARSING_RULE")
public class MessageParseRuleEntity extends WebutilsEntity
{
	/**
	 * Name to uniquely identify the parsing rulr.
	 */
	@Column(name = "NAME", length = 50)
	@UniqueConstraint(name = "NAME")
	private String name;
	
	/**
	 * Agent of user with this role only
	 * will be having access to use this rule.
	 */
	@Column(name = "USER_ROLE", length = 100)
	@DataTypeMapping(converterType = JsonWithTypeConverter.class, type = DataType.STRING)
	private Object userRole;
	
	/**
	 * User role in string format.
	 */
	@Column(name = "USER_ROLE_STR", length = 100)
	private String userRoleString;

	/**
	 * If specified, when sending this rule to agent, employee contact details with
	 * specified role will also be attached. So on need basis, other employees can be contacted
	 * directly. 
	 */
	@Column(name = "TARGET_USER_ROLE", length = 100)
	@DataTypeMapping(converterType = JsonWithTypeConverter.class, type = DataType.STRING)
	private Object targetUserRole;
	
	/**
	 * From type to be matched.
	 */
	@Column(name = "FROM_TYPE", length = 200)
	private String fromType;
	
	/**
	 * From address pattern to be used.
	 */
	@Column(name = "FROM_ADDR_PTRN", length = 500)
	private String fromAddressPattern;
	
	/**
	 * Filter pattern only when matched, the target message will be processed to alert.
	 */
	@Column(name = "MSG_FILTER_PTRN", length = 2000)
	@DataTypeMapping(converterType = JsonWithTypeConverter.class, type = DataType.STRING)
	private Set<String> messageFilterPatterns;
	
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
	 * Gets the name to uniquely identify the parsing rulr.
	 *
	 * @return the name to uniquely identify the parsing rulr
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name to uniquely identify the parsing rulr.
	 *
	 * @param name the new name to uniquely identify the parsing rulr
	 */
	public void setName(String name)
	{
		this.name = name;
	}

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
	public Set<String> getMessageFilterPatterns()
	{
		return messageFilterPatterns;
	}

	/**
	 * Sets the filter pattern only when matched, the target message will be processed to alert.
	 *
	 * @param messageFilterPatterns the new filter pattern only when matched, the target message will be processed to alert
	 */
	public void setMessageFilterPatterns(Set<String> messageFilterPatterns)
	{
		this.messageFilterPatterns = messageFilterPatterns;
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

	/**
	 * Gets the if specified, when sending this rule to agent, employee contact details with specified role will also be attached. So on need basis, other employees can be contacted directly.
	 *
	 * @return the if specified, when sending this rule to agent, employee contact details with specified role will also be attached
	 */
	public Object getTargetUserRole()
	{
		return targetUserRole;
	}

	/**
	 * Sets the if specified, when sending this rule to agent, employee contact details with specified role will also be attached. So on need basis, other employees can be contacted directly.
	 *
	 * @param targetUserRole the new if specified, when sending this rule to agent, employee contact details with specified role will also be attached
	 */
	public void setTargetUserRole(Object targetUserRole)
	{
		this.targetUserRole = targetUserRole;
	}

	/**
	 * Gets the from type to be matched.
	 *
	 * @return the from type to be matched
	 */
	public String getFromType()
	{
		return fromType;
	}

	/**
	 * Sets the from type to be matched.
	 *
	 * @param fromType the new from type to be matched
	 */
	public void setFromType(String fromType)
	{
		this.fromType = fromType;
	}

	/**
	 * Gets the user role in string format.
	 *
	 * @return the user role in string format
	 */
	public String getUserRoleString()
	{
		return userRoleString;
	}

	/**
	 * Sets the user role in string format.
	 *
	 * @param userRoleString the new user role in string format
	 */
	public void setUserRoleString(String userRoleString)
	{
		this.userRoleString = userRoleString;
	}
}
