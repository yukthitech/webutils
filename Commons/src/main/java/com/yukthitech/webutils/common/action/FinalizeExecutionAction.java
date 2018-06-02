package com.yukthitech.webutils.common.action;

/**
 * Action to finalize action plan.
 *  
 * @author akiran
 */
public class FinalizeExecutionAction implements IAgentAction
{
	/**
	 * Name of the action.
	 */
	private String name;
	
	/**
	 * Sets the name of the action.
	 *
	 * @param name the new name of the action
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see com.yukthitech.webutils.common.action.IAgentAction#getName()
	 */
	@Override
	public String getName()
	{
		return name;
	}

	/**
	 * Gets the label to be used for action.
	 *
	 * @return the label to be used for action
	 */
	public String getLabel()
	{
		return name;
	}
}
