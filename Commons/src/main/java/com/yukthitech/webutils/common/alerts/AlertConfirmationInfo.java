package com.yukthitech.webutils.common.alerts;

/**
 * Encapsulation of actual alert data and process data.
 * @author akiran
 */
public class AlertConfirmationInfo
{
	/**
	 * Actual alert data.
	 */
	private Object alertData;
	
	/**
	 * Processing details obtained as part of alert.
	 */
	private AlertProcessedDetails alertProcessedDetails;
	
	/**
	 * Instantiates a new alert confirmation info.
	 */
	public AlertConfirmationInfo()
	{}

	/**
	 * Instantiates a new alert confirmation info.
	 *
	 * @param alertData the alert data
	 * @param alertProcessedDetails the alert processed details
	 */
	public AlertConfirmationInfo(Object alertData, AlertProcessedDetails alertProcessedDetails)
	{
		this.alertData = alertData;
		this.alertProcessedDetails = alertProcessedDetails;
	}

	/**
	 * Gets the actual alert data.
	 *
	 * @return the actual alert data
	 */
	public Object getAlertData()
	{
		return alertData;
	}

	/**
	 * Sets the actual alert data.
	 *
	 * @param alertData the new actual alert data
	 */
	public void setAlertData(Object alertData)
	{
		this.alertData = alertData;
	}

	/**
	 * Gets the processing details obtained as part of alert.
	 *
	 * @return the processing details obtained as part of alert
	 */
	public AlertProcessedDetails getAlertProcessedDetails()
	{
		return alertProcessedDetails;
	}

	/**
	 * Sets the processing details obtained as part of alert.
	 *
	 * @param alertProcessedDetails the new processing details obtained as part of alert
	 */
	public void setAlertProcessedDetails(AlertProcessedDetails alertProcessedDetails)
	{
		this.alertProcessedDetails = alertProcessedDetails;
	}
}
