package com.yukthitech.webutils.alerts;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Table;

import com.yukthitech.persistence.annotations.DataType;
import com.yukthitech.persistence.annotations.DataTypeMapping;
import com.yukthitech.persistence.conversion.impl.JsonWithTypeConverter;
import com.yukthitech.webutils.common.alerts.PullAlertStatus;
import com.yukthitech.webutils.repository.WebutilsEntity;

/**
 * Represents the pull alerts to be processed.
 */
@Table(name = "WEBUTILS_PULL_ALERTS")
public class PullAlertEntity extends WebutilsEntity
{
	/**
	 * Source which is generating this alert.
	 */
	@Column(name = "SOURCE", length = 200)
	private String source;
	
	/**
	 * Title of the alert.
	 */
	@Column(name = "TITLE", length = 500)
	private String title;
	
	/**
	 * Message to be sent.
	 */
	@Column(name = "MESSAGE", length = 2000)
	private String message;
	
	/**
	 * Data to be sent along with alert.
	 */
	@Column(name = "DATA", length = 2000)
	@DataTypeMapping(converterType = JsonWithTypeConverter.class, type = DataType.STRING)
	private Object data;
	
	/**
	 * Alert type.
	 */
	@Column(name = "ALERT_TYPE", length = 100)
	@DataTypeMapping(type = DataType.STRING)
	private Object alertType;

	/**
	 * Alert status.
	 */
	@Column(name = "STATUS", length = 20, nullable = false)
	@DataTypeMapping(type = DataType.STRING)
	private PullAlertStatus status;
	
	/**
	 * Target to which alert should be sent.
	 */
	@Column(name = "TARGET", length = 100, nullable = false)
	private String target;

	/**
	 * Flags indicating different flags for alert.
	 */
	@Column(name = "FLAGS")
	private int flags;

	/**
	 * Actions that can be taken on this alert.
	 */
	@Column(name = "ACTIONS", length = 100)
	@DataTypeMapping(converterType = JsonWithTypeConverter.class, type = DataType.STRING)
	private List<String> actions;
	
	/**
	 * Action used to close alert.
	 */
	@Column(name = "CLOSE_ACTION", length = 15)
	private String closeAction;

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
	 * Gets the alert status.
	 *
	 * @return the alert status
	 */
	public PullAlertStatus getStatus()
	{
		return status;
	}

	/**
	 * Sets the alert status.
	 *
	 * @param status the new alert status
	 */
	public void setStatus(PullAlertStatus status)
	{
		this.status = status;
	}

	/**
	 * Gets the target to which alert should be sent.
	 *
	 * @return the target to which alert should be sent
	 */
	public String getTarget()
	{
		return target;
	}

	/**
	 * Sets the target to which alert should be sent.
	 *
	 * @param target the new target to which alert should be sent
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
	 * Gets the actions that can be taken on this alert.
	 *
	 * @return the actions that can be taken on this alert
	 */
	public List<String> getActions()
	{
		return actions;
	}

	/**
	 * Sets the actions that can be taken on this alert.
	 *
	 * @param actions the new actions that can be taken on this alert
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
