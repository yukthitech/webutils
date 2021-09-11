package com.yukthitech.webutils.parserules.mail;

import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Table;

import com.yukthitech.persistence.annotations.DataType;
import com.yukthitech.persistence.annotations.DataTypeMapping;
import com.yukthitech.persistence.annotations.Indexed;
import com.yukthitech.persistence.conversion.impl.JsonWithTypeConverter;
import com.yukthitech.webutils.repository.WebutilsBaseEntity;

/**
 * Indicates rule which is used to convert message (sms or mail) into an alert.
 * @author akiran
 */
@Table(name = "WEBUTILS_MAIL_PARSE_RULES")
public class MailParseRuleEntity extends WebutilsBaseEntity
{
	/**
	 * Name of the rule.
	 */
	@Column(name = "NAME", length = 100, nullable = false)
	private String name;
	
	/**
	 * Agent of user with this role only
	 * will be having access to use this rule.
	 */
	@Column(name = "USER_ROLE", length = 100, nullable = false)
	@DataTypeMapping(converterType = JsonWithTypeConverter.class, type = DataType.STRING)
	private Object userRole;
	
	/**
	 * User role in string format.
	 */
	@Indexed
	@Column(name = "USER_ROLE_STR", length = 100, nullable = false)
	private String userRoleString;

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
	@Column(name = "SUBJECT_FILTER_PTRNS", length = 2000)
	@DataTypeMapping(converterType = JsonWithTypeConverter.class, type = DataType.STRING)
	private Set<String> subjectFilterPatterns;
	
	/**
	 * This pattern will be searched with in pattern. And the named groups found in the message
	 * will be used to construct alert bean, if specified.
	 */
	@Column(name = "SUBJECT_PTRNS", length = 2000)
	@DataTypeMapping(converterType = JsonWithTypeConverter.class, type = DataType.STRING)
	private Set<String> subjectPatterns;

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
	 * Action plan to be execute when match is found.
	 */
	@Column(name = "ACTION_PLAN_XML")
	@DataTypeMapping(type = DataType.CLOB)
	private String actionPlanXml;
	
	/**
	 * Patterns to recognize attachments, which in turn can be referred as attachments in action plan.
	 */
	@Column(name = "ATTACH_PTRNS", length = 1000)
	@DataTypeMapping(converterType = JsonWithTypeConverter.class, type = DataType.STRING)
	private Map<String, Set<String>> attachmentPatterns;
	
	/**
	 * Flags of this mail parsing rule.
	 */
	@Column(name = "FLAGS")
	private int flags;
	
	/**
	 * Direct action to be performed on the mail.
	 */
	@Column(name = "DIRECT_ACTION")
	private String directAction;

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

	/**
	 * Gets the agent of user with this role only will be having access to use this rule.
	 *
	 * @return the agent of user with this role only will be having access to use this rule
	 */
	public Object getUserRole()
	{
		return userRole;
	}

	/**
	 * Sets the agent of user with this role only will be having access to use this rule.
	 *
	 * @param userRole the new agent of user with this role only will be having access to use this rule
	 */
	public void setUserRole(Object userRole)
	{
		this.userRole = userRole;
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
	public Set<String> getSubjectFilterPatterns()
	{
		return subjectFilterPatterns;
	}

	/**
	 * Sets the filter pattern only when matched, the target message will be processed to alert.
	 *
	 * @param subjectFilterPatterns the new filter pattern only when matched, the target message will be processed to alert
	 */
	public void setSubjectFilterPatterns(Set<String> subjectFilterPatterns)
	{
		this.subjectFilterPatterns = subjectFilterPatterns;
	}

	/**
	 * Gets the this pattern will be searched with in pattern. And the named groups found in the message will be used to construct alert bean, if specified.
	 *
	 * @return the this pattern will be searched with in pattern
	 */
	public Set<String> getSubjectPatterns()
	{
		return subjectPatterns;
	}

	/**
	 * Sets the this pattern will be searched with in pattern. And the named groups found in the message will be used to construct alert bean, if specified.
	 *
	 * @param subjectPatterns the new this pattern will be searched with in pattern
	 */
	public void setSubjectPatterns(Set<String> subjectPatterns)
	{
		this.subjectPatterns = subjectPatterns;
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
	 * Gets the action plan to be execute when match is found.
	 *
	 * @return the action plan to be execute when match is found
	 */
	public String getActionPlanXml()
	{
		return actionPlanXml;
	}

	/**
	 * Sets the action plan to be execute when match is found.
	 *
	 * @param actionPlanXml the new action plan to be execute when match is found
	 */
	public void setActionPlanXml(String actionPlanXml)
	{
		this.actionPlanXml = actionPlanXml;
	}

	/**
	 * Gets the patterns to recognize attachments, which in turn can be referred as attachments in action plan.
	 *
	 * @return the patterns to recognize attachments, which in turn can be referred as attachments in action plan
	 */
	public Map<String, Set<String>> getAttachmentPatterns()
	{
		return attachmentPatterns;
	}

	/**
	 * Sets the patterns to recognize attachments, which in turn can be referred as attachments in action plan.
	 *
	 * @param attachmentPatterns the new patterns to recognize attachments, which in turn can be referred as attachments in action plan
	 */
	public void setAttachmentPatterns(Map<String, Set<String>> attachmentPatterns)
	{
		this.attachmentPatterns = attachmentPatterns;
	}

	/**
	 * Gets the flags of this mail parsing rule.
	 *
	 * @return the flags of this mail parsing rule
	 */
	public int getFlags()
	{
		return flags;
	}

	/**
	 * Sets the flags of this mail parsing rule.
	 *
	 * @param flags the new flags of this mail parsing rule
	 */
	public void setFlags(int flags)
	{
		this.flags = flags;
	}

	/**
	 * Gets the direct action to be performed on the mail.
	 *
	 * @return the direct action to be performed on the mail
	 */
	public String getDirectAction()
	{
		return directAction;
	}

	/**
	 * Sets the direct action to be performed on the mail.
	 *
	 * @param directAction the new direct action to be performed on the mail
	 */
	public void setDirectAction(String directAction)
	{
		this.directAction = directAction;
	}
}
