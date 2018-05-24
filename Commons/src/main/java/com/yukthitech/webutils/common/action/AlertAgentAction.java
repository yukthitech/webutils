package com.yukthitech.webutils.common.action;

import com.yukthitech.webutils.common.alerts.AlertDetails;

/**
 * Action for sending alert.
 */
public class AlertAgentAction extends AbstractAgentAction
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
} 