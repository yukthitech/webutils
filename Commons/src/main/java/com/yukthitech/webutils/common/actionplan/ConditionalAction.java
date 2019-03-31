package com.yukthitech.webutils.common.actionplan;

import java.util.ArrayList;
import java.util.List;

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
	private List<IAgentAction> actions;
	
	/**
	 * Condition to be used for executing main action.
	 */
	private StepCondition condition;

	public List<IAgentAction> getActions()
	{
		return actions;
	}

	public void setActions(List<IAgentAction> actions)
	{
		this.actions = actions;
	}

	/**
	 * Sets the main action.
	 *
	 * @param action the new main action
	 */
	@Override
	public void addAction(IAgentAction action)
	{
		if(this.actions == null)
		{
			this.actions = new ArrayList<>();
		}
		
		this.actions.add(action);
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
}
