package com.yukthitech.webutils.common.actionplan;

import com.yukthitech.webutils.common.action.IAgentAction;

/**
 * A wrapper to wrap action with condition. Only when condition is satisfied then only
 * the main action will be executed.
 * @author akiran
 */
public class ConditionalAction extends AbstractMultiActionSupport implements IAgentAction
{
	/**
	 * Main action.
	 */
	private IAgentAction action;
	
	/**
	 * Condition to be used for executing main action.
	 */
	private StepCondition condition;
	
	/**
	 * Gets the main action.
	 *
	 * @return the main action
	 */
	public IAgentAction getAction()
	{
		return action;
	}

	/**
	 * Sets the main action.
	 *
	 * @param action the new main action
	 */
	public void setAction(IAgentAction action)
	{
		this.action = action;
	}
	
	/* (non-Javadoc)
	 * @see com.yukthitech.webutils.common.actionplan.AbstractMultiActionSupport#addAction(com.yukthitech.webutils.common.action.IAgentAction)
	 */
	@Override
	public void addAction(IAgentAction action)
	{
		setAction(action);
	}

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
		return action.getName();
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.webutils.common.action.IAgentAction#getLabel()
	 */
	@Override
	public String getLabel()
	{
		return action.getName();
	}
}
