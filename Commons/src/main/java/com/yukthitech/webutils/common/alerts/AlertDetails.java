package com.yukthitech.webutils.common.alerts;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yukthitech.utils.BitHelper;
import com.yukthitech.webutils.common.FileInfo;
import com.yukthitech.webutils.common.annotations.IgnoreField;
import com.yukthitech.webutils.common.annotations.Model;

/**
 * Represents an alert.
 * @author akiran
 */
@Model
public class AlertDetails
{
	/**
	 * Flag indicating if confirmation is required for alert.
	 */
	public static final int FLAG_CONFIRMATION_REQUIRED = 0b1;
	
	/**
	 * Flag indicating this is a silent alert and end user should not be 
	 * disturbed with this alert.
	 */
	public static final int FLAG_SILENT_ALERT = 0b10;
	
	/**
	 * Flag indicating this is confirmation alert.
	 */
	public static final int FLAGS_CONFIRMATION_ALERT = 0b100;
	
	/**
	 * Id of the alert.
	 */
	private long id;
	
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
	@IgnoreField
	private Object data;
	
	/**
	 * Alert type.
	 */
	@IgnoreField
	private Object alertType;
	
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
	 * Non persistent internal field. Target agent types, if specified, only those will be invoked to process
	 * the alert.
	 */
	private Set<String> targetAgentTypes;
	
	/**
	 * Status of the alert.
	 */
	private PullAlertStatus status;
	
	/**
	 * Actions available with this alert.
	 */
	private List<String> actions;

	/**
	 * Action used to close alert.
	 */
	private String closeAction;

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
	public Object getAlertType()
	{
		return alertType;
	}

	/**
	 * Sets the alert type.
	 *
	 * @param alertType the new alert type
	 */
	public void setAlertType(Object alertType)
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
	 * Checks if is flag indicating if this alert requires confirmation of alert recieval by target.
	 *
	 * @return the flag indicating if this alert requires confirmation of alert recieval by target
	 */
	@JsonIgnore
	public boolean isConfirmationRequired()
	{
		return BitHelper.isSet(flags, FLAG_CONFIRMATION_REQUIRED);
	}

	/**
	 * Sets the flag indicating if this alert requires confirmation of alert recieval by target.
	 *
	 * @param requiresConfirmation the new flag indicating if this alert requires confirmation of alert recieval by target
	 */
	public void setConfirmationRequired(boolean requiresConfirmation)
	{
		if(requiresConfirmation)
		{
			flags = BitHelper.setFlag(flags, FLAG_CONFIRMATION_REQUIRED);
		}
		else
		{
			flags = BitHelper.unsetFlag(flags, FLAG_CONFIRMATION_REQUIRED);
		}
	}
	
	/**
	 * Checks if is flag indicating if this alert is silent alert.
	 *
	 * @return the flag indicating if this alert is silent alert
	 */
	@JsonIgnore
	public boolean isSilentAlert()
	{
		return BitHelper.isSet(flags, FLAG_SILENT_ALERT);
	}

	/**
	 * Sets the flag indicating if this alert is silent alert.
	 *
	 * @param silentAlert the new flag indicating if this alert is silent alert
	 */
	public void setSilentAlert(boolean silentAlert)
	{
		if(silentAlert)
		{
			flags = BitHelper.setFlag(flags, FLAG_SILENT_ALERT);
		}
		else
		{
			flags = BitHelper.unsetFlag(flags, FLAG_SILENT_ALERT);
		}
	}
	
	/**
	 * Checks and returns if this is confirmation alert.
	 * @return true if confirmation alert
	 */
	public boolean isConfirmationAlert()
	{
		return BitHelper.isSet(flags, FLAGS_CONFIRMATION_ALERT);
	}
	
	/**
	 * Sets the flag indicating if this is confirmation alert.
	 * @param flag true if confirmation alert.
	 */
	public void setConfirmationAlert(boolean flag)
	{
		if(flag)
		{
			flags = BitHelper.setFlag(flags, FLAGS_CONFIRMATION_ALERT);
		}
		else
		{
			flags = BitHelper.unsetFlag(flags, FLAGS_CONFIRMATION_ALERT);
		}
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
	 * Gets the target agent types, if specified, only those will be invoked to process the alert.
	 *
	 * @return the target agent types, if specified, only those will be invoked to process the alert
	 */
	public Set<String> getTargetAgentTypes()
	{
		return targetAgentTypes;
	}

	/**
	 * Sets the target agent types, if specified, only those will be invoked to process the alert.
	 *
	 * @param targetAgentTypes the new target agent types, if specified, only those will be invoked to process the alert
	 */
	public void setTargetAgentTypes(Set<String> targetAgentTypes)
	{
		this.targetAgentTypes = targetAgentTypes;
	}
	
	/**
	 * Adds specified target agent type.
	 *
	 * @param type type to add
	 */
	public void addTargetAgentType(String type)
	{
		if(this.targetAgentTypes == null)
		{
			this.targetAgentTypes = new HashSet<>();
		}
		
		this.targetAgentTypes.add(type);
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
	 * Gets the actions available with this alert.
	 *
	 * @return the actions available with this alert
	 */
	public List<String> getActions()
	{
		return actions;
	}

	/**
	 * Sets the actions available with this alert.
	 *
	 * @param actions the new actions available with this alert
	 */
	public void setActions(List<String> actions)
	{
		this.actions = actions;
	}

	/**
	 * Gets the action used to close alert.
	 *
	 * @return the action used to close alert
	 */
	public String getCloseAction()
	{
		return closeAction;
	}

	/**
	 * Sets the action used to close alert.
	 *
	 * @param closeAction the new action used to close alert
	 */
	public void setCloseAction(String closeAction)
	{
		this.closeAction = closeAction;
	}
}
