package com.yukthitech.webutils.common.actionplan;

import java.util.ArrayList;
import java.util.List;

import com.yukthitech.webutils.common.action.IAgentAction;

/**
 * Action plan step along with required configuration.
 * @author akiran
 */
public class ActionPlanStep extends AbstractMultiActionSupport
{
	/**
	 * Condition to be satisfied for execution of this steps.
	 */
	private StepCondition condition;
	
	/**
	 * List of actions to be executed as part of this step.
	 */
	private List<IAgentAction> actions;

	/**
	 * Gets the condition to be satisfied for execution of this steps.
	 *
	 * @return the condition to be satisfied for execution of this steps
	 */
	public StepCondition getCondition()
	{
		return condition;
	}

	/**
	 * Sets the condition to be satisfied for execution of this steps.
	 *
	 * @param condition the new condition to be satisfied for execution of this steps
	 */
	public void setCondition(StepCondition condition)
	{
		this.condition = condition;
	}

	/**
	 * Gets the list of actions to be executed as part of this step.
	 *
	 * @return the list of actions to be executed as part of this step
	 */
	public List<IAgentAction> getActions()
	{
		return actions;
	}

	/**
	 * Sets the list of actions to be executed as part of this step.
	 *
	 * @param actions the new list of actions to be executed as part of this step
	 */
	public void setActions(List<IAgentAction> actions)
	{
		this.actions = actions;
	}
	
	/**
	 * Adds action to current step.
	 * @param action action to add.
	 */
	public void addAction(IAgentAction action)
	{
		if(this.actions == null)
		{
			this.actions = new ArrayList<>();
		}
		
		this.actions.add(action);
	}
}
