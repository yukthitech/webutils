package com.yukthitech.webutils.common.alerts;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yukthitech.utils.BitHelper;
import com.yukthitech.webutils.common.FileInfo;
import com.yukthitech.webutils.common.IWebUtilsCommonConstants;
import com.yukthitech.webutils.common.action.IAgentAction;
import com.yukthitech.webutils.common.actionplan.ActionPlan;
import com.yukthitech.webutils.common.annotations.IgnoreField;
import com.yukthitech.webutils.common.annotations.Model;
import com.yukthitech.webutils.common.annotations.json.DataWithDynamicTypes;
import com.yukthitech.webutils.common.annotations.xml.DataWithXml;

/**
 * Represents an alert.
 * @author akiran
 */
@Model
public class AlertDetails
{
	/**
	 * Id of the alert.
	 */
	private long id;
	
	/**
	 * Used to identify alert uniquely.
	 */
	private String dynamicId;
	
	/**
	 * Used to identify alert and its confirmation uniquely.
	 */
	private String name;
	
	/**
	 * Source which is generating this alert.
	 */
	private String source;
	
	/**
	 * Title of the alert.
	 */
	private String title;
	
	/**
	 * Message to be sent.
	 */
	private String message;
	
	/**
	 * Non persisted field. Detailed message that can be used by alert agents which can handle long messages.
	 */
	private String longMessage;
	
	/**
	 * Data to be sent along with alert.
	 */
	@DataWithDynamicTypes
	@IgnoreField
	private Object data;
	
	/**
	 * Alert type.
	 */
	@DataWithDynamicTypes
	@IgnoreField
	private IAlertType alertType;
	
	/**
	 * Used by pull alerts.
	 */
	private String target;
	
	/**
	 * Optional file that can be attached to alert.
	 */
	private FileInfo file;
	
	/**
	 * Flags indicating different flags for alert.
	 */
	private int flags;
	
	/**
	 * Status of the alert.
	 */
	private PullAlertStatus status;
	
	/**
	 * Actions to be associated with this task.
	 */
	@DataWithDynamicTypes
	@IgnoreField
	private List<IAgentAction> actions;
	
	/**
	 * Alert processing details, used internally.
	 */
	@IgnoreField
	private AlertProcessedDetails alertProcessedDetails;
	
	/**
	 * Action plan to be execute when match is found.
	 */
	@DataWithXml
	@IgnoreField
	private ActionPlan actionPlan;

	/**
	 * Factory method to created silent alert with specified details.
	 * @param source source of alert
	 * @param alertType type of alert
	 * @param title title to use
	 * @param message message to display
	 * @return new alert details
	 */
	public static AlertDetails newAlert(String source, IAlertType alertType, String title, String message)
	{
		AlertDetails alertDetails = new AlertDetails();
		alertDetails.setSource(source);
		alertDetails.setTitle(title);
		alertDetails.setMessage(message);
		alertDetails.setAlertType(alertType);
		
		return alertDetails;
	}

	/**
	 * Factory method to created silent alert with specified details.
	 * @param source source of alert
	 * @param alertType type of alert
	 * @param title title to use
	 * @param message message to display
	 * @return new alert details
	 */
	public static AlertDetails newSilentAlert(String source, IAlertType alertType, String title, String message)
	{
		AlertDetails alertDetails = newAlert(source, alertType, title, message);
		alertDetails.setSilentAlert(true);
		
		return alertDetails;
	}
	
	/**
	 * Gets the id of the alert.
	 *
	 * @return the id of the alert
	 */
	public long getId()
	{
		return id;
	}

	/**
	 * Sets the id of the alert.
	 *
	 * @param id the new id of the alert
	 */
	public void setId(long id)
	{
		this.id = id;
	}
	
	/**
	 * Gets the used to identify alert and its confirmation uniquely.
	 *
	 * @return the used to identify alert and its confirmation uniquely
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the used to identify alert and its confirmation uniquely.
	 *
	 * @param name the new used to identify alert and its confirmation uniquely
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the source which is generating this alert.
	 *
	 * @return the source which is generating this alert
	 */
	public String getSource()
	{
		return source;
	}

	/**
	 * Sets the source which is generating this alert.
	 *
	 * @param source the new source which is generating this alert
	 */
	public void setSource(String source)
	{
		this.source = source;
	}

	/**
	 * Gets the title of the alert.
	 *
	 * @return the title of the alert
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * Sets the title of the alert.
	 *
	 * @param title the new title of the alert
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}

	/**
	 * Gets the message to be sent.
	 *
	 * @return the message to be sent
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * Sets the message to be sent.
	 *
	 * @param message the new message to be sent
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}

	/**
	 * Gets the data to be sent along with alert.
	 *
	 * @return the data to be sent along with alert
	 */
	public Object getData()
	{
		return data;
	}

	/**
	 * Sets the data to be sent along with alert.
	 *
	 * @param data the new data to be sent along with alert
	 */
	public void setData(Object data)
	{
		this.data = data;
	}

	/**
	 * Gets the alert type.
	 *
	 * @return the alert type
	 */
	public IAlertType getAlertType()
	{
		return alertType;
	}

	/**
	 * Sets the alert type.
	 *
	 * @param alertType the new alert type
	 */
	public void setAlertType(IAlertType alertType)
	{
		this.alertType = alertType;
	}

	/**
	 * Gets the optional file that can be attached to alert.
	 *
	 * @return the optional file that can be attached to alert
	 */
	public FileInfo getFile()
	{
		return file;
	}

	/**
	 * Sets the optional file that can be attached to alert.
	 *
	 * @param file the new optional file that can be attached to alert
	 */
	public void setFile(FileInfo file)
	{
		this.file = file;
	}

	/**
	 * Gets the used by pull alerts.
	 *
	 * @return the used by pull alerts
	 */
	public String getTarget()
	{
		return target;
	}

	/**
	 * Sets the used by pull alerts.
	 *
	 * @param target the new used by pull alerts
	 */
	public void setTarget(String target)
	{
		this.target = target;
	}
	
	/**
	 * Gets the flags indicating different flags for alert.
	 *
	 * @return the flags indicating different flags for alert
	 */
	public int getFlags()
	{
		return flags;
	}

	/**
	 * Sets the flags indicating different flags for alert.
	 *
	 * @param flags the new flags indicating different flags for alert
	 */
	public void setFlags(int flags)
	{
		this.flags = flags;
	}
	
	/**
	 * Checks if specified flag is set.
	 * @param flag flag to check
	 * @return true if specified flag is set
	 */
	public boolean isFlagSet(int flag)
	{
		return BitHelper.isSet(flags, flag);
	}
	
	/**
	 * Sets/unsets the specified flag.
	 * @param flag flag to set or unser
	 * @param set flag indicating if the specified flag should be set or unset.
	 */
	public void setFlag(int flag, boolean set)
	{
		if(set)
		{
			flags = BitHelper.setFlag(flags, flag);
		}
		else
		{
			flags = BitHelper.unsetFlag(flags, flag);
		}
	}

	/**
	 * Checks if is flag indicating if this alert requires confirmation of alert recieval by target.
	 *
	 * @return the flag indicating if this alert requires confirmation of alert recieval by target
	 */
	@JsonIgnore
	public boolean isConfirmationRequired()
	{
		return isFlagSet(IWebUtilsCommonConstants.ALERT_FLAG_CONFIRMATION_REQUIRED);
	}

	/**
	 * Sets the flag indicating if this alert requires confirmation of alert recieval by target.
	 *
	 * @param requiresConfirmation the new flag indicating if this alert requires confirmation of alert recieval by target
	 */
	public void setConfirmationRequired(boolean requiresConfirmation)
	{
		setFlag(IWebUtilsCommonConstants.ALERT_FLAG_CONFIRMATION_REQUIRED, requiresConfirmation);
	}
	
	/**
	 * Checks if is flag indicating if this alert is silent alert.
	 *
	 * @return the flag indicating if this alert is silent alert
	 */
	@JsonIgnore
	public boolean isSilentAlert()
	{
		return isFlagSet(IWebUtilsCommonConstants.ALERT_FLAG_SILENT_ALERT);
	}

	/**
	 * Sets the flag indicating if this alert is silent alert.
	 *
	 * @param silentAlert the new flag indicating if this alert is silent alert
	 */
	public void setSilentAlert(boolean silentAlert)
	{
		setFlag(IWebUtilsCommonConstants.ALERT_FLAG_SILENT_ALERT, silentAlert);
	}
	
	/**
	 * Checks and returns if this is confirmation alert.
	 * @return true if confirmation alert
	 */
	@JsonIgnore
	public boolean isConfirmationAlert()
	{
		return isFlagSet(IWebUtilsCommonConstants.ALERT_FLAG_CONFIRMATION_ALERT);
	}
	
	/**
	 * Sets the flag indicating if this is confirmation alert.
	 * @param flag true if confirmation alert.
	 */
	public void setConfirmationAlert(boolean flag)
	{
		setFlag(IWebUtilsCommonConstants.ALERT_FLAG_CONFIRMATION_ALERT, flag);
	}

	/**
	 * Checks if app specific flag1 is set.
	 * @return true if set
	 */
	@JsonIgnore
	public boolean isAppSpecificFlag1()
	{
		return isFlagSet(IWebUtilsCommonConstants.ALERT_FLAG_APP_SPECIFIC_1);
	}
	
	/**
	 * Sets the app specific flag 1.
	 * @param flag true if flag needs to be set.
	 */
	public void setAppSpecificFlag1(boolean flag)
	{
		setFlag(IWebUtilsCommonConstants.ALERT_FLAG_APP_SPECIFIC_1, flag);
	}

	/**
	 * Checks if app specific flag2 is set.
	 * @return true if set
	 */
	@JsonIgnore
	public boolean isAppSpecificFlag2()
	{
		return isFlagSet(IWebUtilsCommonConstants.ALERT_FLAG_APP_SPECIFIC_2);
	}
	
	/**
	 * Sets the app specific flag 2.
	 * @param flag true if flag needs to be set.
	 */
	public void setAppSpecificFlag2(boolean flag)
	{
		setFlag(IWebUtilsCommonConstants.ALERT_FLAG_APP_SPECIFIC_2, flag);
	}

	/**
	 * Checks if app specific flag3 is set.
	 * @return true if set
	 */
	@JsonIgnore
	public boolean isAppSpecificFlag3()
	{
		return isFlagSet(IWebUtilsCommonConstants.ALERT_FLAG_APP_SPECIFIC_3);
	}
	
	/**
	 * Sets the app specific flag 3.
	 * @param flag true if flag needs to be set.
	 */
	public void setAppSpecificFlag3(boolean flag)
	{
		setFlag(IWebUtilsCommonConstants.ALERT_FLAG_APP_SPECIFIC_3, flag);
	}

	/**
	 * Gets the detailed message that can be used by alert agents which can handle long messages.
	 *
	 * @return the detailed message that can be used by alert agents which can handle long messages
	 */
	public String getLongMessage()
	{
		return longMessage;
	}

	/**
	 * Sets the detailed message that can be used by alert agents which can handle long messages.
	 *
	 * @param longMessage the new detailed message that can be used by alert agents which can handle long messages
	 */
	public void setLongMessage(String longMessage)
	{
		this.longMessage = longMessage;
	}

	/**
	 * Gets the status of the alert.
	 *
	 * @return the status of the alert
	 */
	public PullAlertStatus getStatus()
	{
		return status;
	}

	/**
	 * Sets the status of the alert.
	 *
	 * @param status the new status of the alert
	 */
	public void setStatus(PullAlertStatus status)
	{
		this.status = status;
	}
	
	/**
	 * Gets the actions to be associated with this task.
	 *
	 * @return the actions to be associated with this task
	 */
	public List<IAgentAction> getActions()
	{
		return actions;
	}

	/**
	 * Sets the actions to be associated with this task.
	 *
	 * @param actions the new actions to be associated with this task
	 */
	public void setActions(List<IAgentAction> actions)
	{
		this.actions = actions;
	}
	
	/**
	 * Adds specified action to underlying list.
	 * @param action action to add
	 */
	public void addAction(IAgentAction action)
	{
		if(this.actions == null)
		{
			this.actions = new ArrayList<>();
		}
		
		this.actions.add(action);
	}

	/**
	 * Gets the alert processing details, used internally.
	 *
	 * @return the alert processing details, used internally
	 */
	public AlertProcessedDetails getAlertProcessedDetails()
	{
		return alertProcessedDetails;
	}

	/**
	 * Sets the alert processing details, used internally.
	 *
	 * @param alertProcessedDetails the new alert processing details, used internally
	 */
	public void setAlertProcessedDetails(AlertProcessedDetails alertProcessedDetails)
	{
		this.alertProcessedDetails = alertProcessedDetails;
	}
	
	/**
	 * In case if this is confirmation alert and action taken is available, the confirmation
	 * action will be returned. Otherwise null will be returned.
	 * @return confirmation action
	 */
	@JsonIgnore
	public String getConfirmationAction()
	{
		if(!(data instanceof AlertConfirmationInfo))
		{
			return null;
		}
		
		AlertConfirmationInfo confirmation = (AlertConfirmationInfo) data;
		
		if(confirmation.getAlertProcessedDetails() == null)
		{
			return null;
		}
		
		return confirmation.getAlertProcessedDetails().getAction();
	}
	
	/**
	 * In case if this is confirmation alert and action taken is available, the confirmation
	 * action's sub-action will be returned. Otherwise null will be returned.
	 * @return confirmation action
	 */
	@JsonIgnore
	public String getConfirmationSubaction()
	{
		if(!(data instanceof AlertConfirmationInfo))
		{
			return null;
		}
		
		AlertConfirmationInfo confirmation = (AlertConfirmationInfo) data;
		
		if(confirmation.getAlertProcessedDetails() == null)
		{
			return null;
		}
		
		return confirmation.getAlertProcessedDetails().getSubaction();
	}

	/**
	 * Gets the action plan to be execute when match is found.
	 *
	 * @return the action plan to be execute when match is found
	 */
	public ActionPlan getActionPlan()
	{
		return actionPlan;
	}

	/**
	 * Sets the action plan to be execute when match is found.
	 *
	 * @param actionPlan the new action plan to be execute when match is found
	 */
	public void setActionPlan(ActionPlan actionPlan)
	{
		this.actionPlan = actionPlan;
	}

	/**
	 * Gets the used to identify alert uniquely.
	 *
	 * @return the used to identify alert uniquely
	 */
	public String getDynamicId()
	{
		return dynamicId;
	}

	/**
	 * Sets the used to identify alert uniquely.
	 *
	 * @param dynamicId the new used to identify alert uniquely
	 */
	public void setDynamicId(String dynamicId)
	{
		this.dynamicId = dynamicId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}
