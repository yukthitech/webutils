package com.yukthitech.webutils.common.alerts;

import com.yukthitech.webutils.common.controllers.IClientController;
import com.yukthitech.webutils.common.models.BaseResponse;
import com.yukthitech.webutils.common.models.BasicReadListResponse;

/**
 * Controller to access alerts.
 * @author akiran
 * @param <R> Http multi part request
*/
public interface IAlertController<R> extends IClientController<IAlertController<R>>
{
	/**
	 * Used to send alert.
	 * @param alert alert to send.
	 * @param request request to be used.
	 * @return success or failure response.
	 */
	public BaseResponse sendAlert(AlertDetails alert, R request);
	
	/**
	 * Fetches alerts for specified source.
	 * @param source source for which alerts needs to be fetched.
	 * @return matching alerts.
	 */
	public BasicReadListResponse<AlertDetails> fetchAlerts(String source);
	
	/**
	 * Marks the specified alert as processed.
	 * @param id alert id to be processed.
	 * @return success or failure response.
	 */
	public BaseResponse markProcessed(long id);
}
