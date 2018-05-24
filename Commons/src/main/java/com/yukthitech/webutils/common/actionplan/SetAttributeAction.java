package com.yukthitech.webutils.common.actionplan;

import com.yukthitech.webutils.common.action.IAgentAction;

/**
 * Sets the attribute on context if execution is successful.
 * @author akiran
 */
public class SetAttributeAction implements IAgentAction
{
	/**
	 * Name of the action which also will be used to set context 
	 * attribute of result.
	 */
	private String name;
	
	/**
	 * Value to set.
	 */
	private String value;
	
	/**
	 * Condition to be used to set the result.
	 */
	private StepCondition condition;
	
	/**
	 * Gets the condition to be used for executing main action.
	 *
	 * @return the condition to be used for executing main action
	 */
	public StepCondition getCondition()
	{
		return condition;
	}

	/**
	 * Sets the condition to be used for executing main action.
	 *
	 * @param condition the new condition to be used for executing main action
	 */
	public void setCondition(StepCondition condition)
	{
		this.condition = condition;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.webutils.common.action.IAgentAction#getName()
	 */
	@Override
	public String getName()
	{
		return name;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.webutils.common.action.IAgentAction#getLabel()
	 */
	@Override
	public String getLabel()
	{
		return name;
	}

	/**
	 * Gets the value to set.
	 *
	 * @return the value to set
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * Sets the value to set.
	 *
	 * @param value the new value to set
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

	/**
	 * Sets the name of the action which also will be used to set context attribute of result.
	 *
	 * @param name the new name of the action which also will be used to set context attribute of result
	 */
	public void setName(String name)
	{
		this.name = name;
	}
}
