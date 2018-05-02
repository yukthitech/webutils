package com.yukthitech.webutils.common.alerts;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yukthitech.validation.annotations.NotEmpty;
import com.yukthitech.validation.annotations.Required;
import com.yukthitech.webutils.common.annotations.Model;
import com.yukthitech.webutils.common.annotations.json.JsonWithTypeSerializer;

/**
 * Used to send details while marking alert as processed.
 * @author akiran
 */
@Model
public class AlertProcessedDetails
{
	/**
	 * Action used to process alert.
	 */
	@Required
	@NotEmpty
	private String action;
	
	/**
	 * Data accepted from user as part of alert processing.
	 */
	@JsonSerialize(using = JsonWithTypeSerializer.class, as = String.class)
	private Object data;

	/**
	 * Gets the action used to process alert.
	 *
	 * @return the action used to process alert
	 */
	public String getAction()
	{
		return action;
	}

	/**
	 * Sets the action used to process alert.
	 *
	 * @param action the new action used to process alert
	 */
	public void setAction(String action)
	{
		this.action = action;
	}

	/**
	 * Gets the data accepted from user as part of alert processing.
	 *
	 * @return the data accepted from user as part of alert processing
	 */
	public Object getData()
	{
		return data;
	}

	/**
	 * Sets the data accepted from user as part of alert processing.
	 *
	 * @param data the new data accepted from user as part of alert processing
	 */
	public void setData(Object data)
	{
		this.data = data;
	}
}
