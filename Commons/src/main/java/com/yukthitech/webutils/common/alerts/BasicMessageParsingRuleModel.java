package com.yukthitech.webutils.common.alerts;

import java.util.List;

import com.yukthitech.persistence.repository.annotations.Field;
import com.yukthitech.webutils.common.BaseModel;
import com.yukthitech.webutils.common.ContactDetails;
import com.yukthitech.webutils.common.annotations.IgnoreField;
import com.yukthitech.webutils.common.annotations.Model;

/**
 * Indicates basic details of rule which is used to convert message (sms or mail) into an alert.
 * @author akiran
 */
@Model
public class BasicMessageParsingRuleModel extends BaseModel
{
	/**
	 * If specified, when sending this rule to agent, employee contact details with
	 * specified role will also be attached. So on need basis, other employees can be contacted
	 * directly. 
	 */
	@IgnoreField
	@Field("targetUserRole")
	private Object targetUserRole;
	
	/**
	 * Alert event type to be executed when this message parsing rule is
	 * matched.
	 */
	@Field("alertEventType")
	private String alertEventType;
	
	/**
	 * From type to be matched.
	 */
	@Field("fromType")
	private String fromType;
	
	/**
	 * From address pattern to be used.
	 */
	@Field("fromAddressPattern")
	private String fromAddressPattern;
	
	/**
	 * Filter pattern only when matched, the target message will be processed to alert.
	 */
	@Field("messageFilterPattern")
	private String messageFilterPattern;
	
	/**
	 * Target users for this rule, which can be used by client agent directly.
	 */
	@IgnoreField
	private List<ContactDetails> targetUsers;

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
	 * Gets the target users for this rule, which can be used by client agent directly.
	 *
	 * @return the target users for this rule, which can be used by client agent directly
	 */
	public List<ContactDetails> getTargetUsers()
	{
		return targetUsers;
	}

	/**
	 * Sets the target users for this rule, which can be used by client agent directly.
	 *
	 * @param targetUsers the new target users for this rule, which can be used by client agent directly
	 */
	public void setTargetUsers(List<ContactDetails> targetUsers)
	{
		this.targetUsers = targetUsers;
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
}
