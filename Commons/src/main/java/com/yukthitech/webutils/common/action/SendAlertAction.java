package com.yukthitech.webutils.common.action;

import com.yukthitech.webutils.common.alerts.AlertDetails;

/**
 * Action for sending alert.
 */
public class SendAlertAction extends AbstractAgentAction
{
	/**
	 * Alert details.
	 */
	private AlertDetails alert;
	
	/**
	 * Alerts data json.
	 */
	private String alertDataJson;
	
	/**
	 * Flag to indicate that message being sent is also displayed on client.
	 */
	private boolean displayAtClient;

	/**
	 * Gets the alert details.
	 *
	 * @return the alert details
	 */
	public AlertDetails getAlert()
	{
		return alert;
	}

	/**
	 * Sets the alert details.
	 *
	 * @param alert the new alert details
	 */
	public void setAlert(AlertDetails alert)
	{
		this.alert = alert;
	}

	/**
	 * Gets the alerts data json.
	 *
	 * @return the alerts data json
	 */
	public String getAlertDataJson()
	{
		return alertDataJson;
	}

	/**
	 * Sets the alerts data json.
	 *
	 * @param alertDataJson the new alerts data json
	 */
	public void setAlertDataJson(String alertDataJson)
	{
		this.alertDataJson = alertDataJson;
	}

	/**
	 * Checks if is flag to indicate that message being sent is also displayed on client.
	 *
	 * @return the flag to indicate that message being sent is also displayed on client
	 */
	public boolean isDisplayAtClient()
	{
		return displayAtClient;
	}

	/**
	 * Sets the flag to indicate that message being sent is also displayed on client.
	 *
	 * @param displayAtClient the new flag to indicate that message being sent is also displayed on client
	 */
	public void setDisplayAtClient(boolean displayAtClient)
	{
		this.displayAtClient = displayAtClient;
	}
} 