package com.yukthitech.webutils.common.parserules.mail;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yukthitech.persistence.repository.annotations.Field;
import com.yukthitech.utils.BitHelper;
import com.yukthitech.webutils.common.BaseModel;
import com.yukthitech.webutils.common.actionplan.ActionPlan;
import com.yukthitech.webutils.common.annotations.IgnoreField;
import com.yukthitech.webutils.common.annotations.Model;
import com.yukthitech.webutils.common.annotations.xml.DataWithXml;

/**
 * Model for mail parsing rules.
 */
@Model
public class MailParseRuleModel extends BaseModel
{
	/**
	 * Flag indicating if the mail has to be deleted, post processing.
	 */
	public static final int FLAG_DELETE_MAIL = 0b1;
	/**
	 * Name of the rule.
	 */
	@Field("name")
	private String name;

	/**
	 * Agent of user with this role only
	 * will be having access to use this rule.
	 */
	@Field("userRole")
	@IgnoreField
	private Object userRole;
	
	/**
	 * User role in string format.
	 */
	@Field("userRoleString")
	private String userRoleString;

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
	@Field("subjectFilterPatterns")
	private Set<String> subjectFilterPatterns;
	
	/**
	 * This pattern will be searched with in pattern. And the named groups found in the message
	 * will be used to construct alert bean, if specified.
	 */
	@Field("subjectPatterns")
	private Set<String> subjectPatterns;

	/**
	 * This pattern will be searched with in pattern. And the named groups found in the message
	 * will be used to construct alert bean, if specified.
	 */
	@Field("messagePatterns")
	private Set<String> messagePatterns;
	
	/**
	 * Alert event type to be executed when this message parsing rule is
	 * matched.
	 */
	@Field("alertEventType")
	private String alertEventType;
	
	/**
	 * Default attributes to be set when this rule is matched.
	 */
	@Field("defaultAttributes")
	@IgnoreField
	private Map<String, Object> defaultAttributes;
	
	/**
	 * Used only on server side.
	 */
	@Field("actionPlanXml")
	private String actionPlanXml;
	
	/**
	 * Action plan associated with this rule.
	 */
	@DataWithXml
	@IgnoreField
	private ActionPlan actionPlan;
	
	/**
	 * Patterns to recognize attachments, which in turn can be referred as attachments in action plan.
	 */
	@Field("attachmentPatterns")
	@IgnoreField
	private Map<String, Set<String>> attachmentPatterns;
	
	/**
	 * Flags of this mail parsing rule.
	 */
	@Field("flags")
	private int flags;

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
	 * Gets the used only on server side.
	 *
	 * @return the used only on server side
	 */
	@JsonIgnore
	public String getActionPlanXml()
	{
		return actionPlanXml;
	}

	/**
	 * Sets the used only on server side.
	 *
	 * @param actionPlanXml the new used only on server side
	 */
	public void setActionPlanXml(String actionPlanXml)
	{
		this.actionPlanXml = actionPlanXml;
	}

	/**
	 * Gets the action plan associated with this rule.
	 *
	 * @return the action plan associated with this rule
	 */
	public ActionPlan getActionPlan()
	{
		return actionPlan;
	}

	/**
	 * Sets the action plan associated with this rule.
	 *
	 * @param actionPlan the new action plan associated with this rule
	 */
	public void setActionPlan(ActionPlan actionPlan)
	{
		this.actionPlan = actionPlan;
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
	 * Marks mail for delete post processing.
	 * @param flag true if mail needs to be deleted
	 */
	public void setDeleteMail(boolean flag)
	{
		flags = BitHelper.setFlagValue(flags, FLAG_DELETE_MAIL, flag);
	}
	
	/**
	 * Returns flag indicating if the mail needs to be deleted.
	 * @return true if mail needs to be deleted.
	 */
	public boolean isDeleteMail()
	{
		return BitHelper.isSet(flags, FLAG_DELETE_MAIL);
	}
}
