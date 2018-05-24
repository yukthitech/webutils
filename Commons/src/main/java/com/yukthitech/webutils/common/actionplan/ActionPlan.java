package com.yukthitech.webutils.common.actionplan;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents action plan.
 * @author akiran
 */
public class ActionPlan
{
	/**
	 * Steps to execute as part of this plan.
	 */
	private List<ActionPlanStep> steps;

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
}
