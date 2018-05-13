package com.yukthitech.webutils.common.alerts;

import com.yukthitech.webutils.common.annotations.IgnoreField;
import com.yukthitech.webutils.common.annotations.Model;
import com.yukthitech.webutils.common.annotations.json.DataWithDynamicTypes;

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
	private String action;
	
	/**
	 * Subaction choosen by user within the form geneated
	 * by main action.
	 */
	private String subaction;
	
	/**
	 * Data accepted from user as part of alert processing.
	 */
	@IgnoreField
	@DataWithDynamicTypes
	private Object data;
	
	/**
	 * Instantiates a new alert processed details.
	 */
	public AlertProcessedDetails()
	{}
	
	/**
	 * Instantiates a new alert processed details.
	 *
	 * @param action the action
	 * @param data the data
	 */
	public AlertProcessedDetails(String action, Object data)
	{
		this.action = action;
		this.data = data;
	}

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

	/**
	 * Gets the subaction choosen by user within the form geneated by main action.
	 *
	 * @return the subaction choosen by user within the form geneated by main action
	 */
	public String getSubaction()
	{
		return subaction;
	}

	/**
	 * Sets the subaction choosen by user within the form geneated by main action.
	 *
	 * @param subaction the new subaction choosen by user within the form geneated by main action
	 */
	public void setSubaction(String subaction)
	{
		this.subaction = subaction;
	}
}
