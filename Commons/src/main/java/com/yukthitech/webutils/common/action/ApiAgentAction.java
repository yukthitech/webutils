package com.yukthitech.webutils.common.action;

/**
 * Simple action which would simply send event to server.
 *  
 * @author akiran
 */
public class ApiAgentAction extends AbstractAgentAction
{
	/**
	 * Action to be executed.
	 */
	private String action;
	
	/**
	 * Json template to construct parameters for action parameters. 
	 */
	private String parameterJson;
	
	/**
	 * Json template to construct model for action.
	 */
	private String modelJson;
	
	/**
	 * Gets the action to be executed.
	 *
	 * @return the action to be executed
	 */
	public String getAction()
	{
		return action;
	}

	/**
	 * Sets the action to be executed.
	 *
	 * @param action the new action to be executed
	 */
	public void setAction(String action)
	{
		this.action = action;
	}

	/**
	 * Gets the json template to construct parameters for action parameters.
	 *
	 * @return the json template to construct parameters for action parameters
	 */
	public String getParameterJson()
	{
		return parameterJson;
	}

	/**
	 * Sets the json template to construct parameters for action parameters.
	 *
	 * @param parameterJson the new json template to construct parameters for action parameters
	 */
	public void setParameterJson(String parameterJson)
	{
		this.parameterJson = parameterJson;
	}

	/**
	 * Gets the json template to construct model for action.
	 *
	 * @return the json template to construct model for action
	 */
	public String getModelJson()
	{
		return modelJson;
	}

	/**
	 * Sets the json template to construct model for action.
	 *
	 * @param modelJson the new json template to construct model for action
	 */
	public void setModelJson(String modelJson)
	{
		this.modelJson = modelJson;
	}
}
