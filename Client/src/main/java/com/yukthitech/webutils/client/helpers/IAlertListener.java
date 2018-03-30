package com.yukthitech.webutils.client.helpers;

import com.yukthitech.webutils.common.alerts.AlertDetails;

/**
 * Callback that can be used to observe incoming alerts.
 * @author akiran
 */
public interface IAlertListener
{
	/**
	 * Invoked when an alert is received by client.
	 * @param alert received alert
	 * @return should return true to indicate alert is processed.
	 */
	public boolean onAlert(AlertDetails alert);
}
