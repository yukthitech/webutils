package com.yukthitech.webutils.common.actionplan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.yukthitech.webutils.common.action.IAgentAction;

/**
 * Represents action plan.
 * @author akiran
 */
public class ActionPlan
{
	/**
	 * Name of the action plan. That can be used as label for action which in turn
	 * wil execute action plan.
	 */
	private String name;
	
	/**
	 * Steps to execute as part of this plan.
	 */
	private List<ActionPlanStep> steps;
	
	/**
	 * If specified, this action will be executed when action plan execution is finalized.
	 */
	private IAgentAction finalAction;
	
	/**
	 * Context being used to execute action plan.
	 */
	private Map<String, Object> context;

	/**
	 * Gets the name of the action plan. That can be used as label for action which in turn wil execute action plan.
	 *
	 * @return the name of the action plan
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the action plan. That can be used as label for action which in turn wil execute action plan.
	 *
	 * @param name the new name of the action plan
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the steps to execute as part of this plan.
	 *
	 * @return the steps to execute as part of this plan
	 */
	public List<ActionPlanStep> getSteps()
	{
		return steps;
	}

	/**
	 * Sets the steps to execute as part of this plan.
	 *
	 * @param steps the new steps to execute as part of this plan
	 */
	public void setSteps(List<ActionPlanStep> steps)
	{
		this.steps = steps;
	}
	
	/**
	 * Adds the step to current plan.
	 * @param step step to add.
	 */
	public void addStep(ActionPlanStep step)
	{
		if(steps == null)
		{
			steps = new ArrayList<>();
		}
		
		steps.add(step);
	}

	/**
	 * Gets the if specified, this action will be executed when action plan execution is finalized.
	 *
	 * @return the if specified, this action will be executed when action plan execution is finalized
	 */
	public IAgentAction getFinalAction()
	{
		return finalAction;
	}

	/**
	 * Sets the if specified, this action will be executed when action plan execution is finalized.
	 *
	 * @param finalAction the new if specified, this action will be executed when action plan execution is finalized
	 */
	public void setFinalAction(IAgentAction finalAction)
	{
		this.finalAction = finalAction;
	}

	/**
	 * Gets the context being used to execute action plan.
	 *
	 * @return the context being used to execute action plan
	 */
	public Map<String, Object> getContext()
	{
		return context;
	}

	/**
	 * Sets the context being used to execute action plan.
	 *
	 * @param context the new context being used to execute action plan
	 */
	public void setContext(Map<String, Object> context)
	{
		this.context = context;
	}
}
