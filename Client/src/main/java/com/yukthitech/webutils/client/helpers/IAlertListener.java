package com.yukthitech.webutils.client.helpers;

import com.yukthitech.utils.ObjectWrapper;
import com.yukthitech.webutils.common.alerts.AlertDetails;
import com.yukthitech.webutils.common.alerts.AlertProcessedDetails;

/**
 * Callback that can be used to observe incoming alerts.
 * @author akiran
 */
public interface IAlertListener
{
	/**
	 * Invoked when an alert is received by client.
	 * @param alert received alert
	 * @param confirmationData data to be sent to alert as part of confirmation.
	 * @return should return true to indicate alert is processed.
	 */
	public boolean onAlert(AlertDetails alert, ObjectWrapper<AlertProcessedDetails> confirmationData);
}
