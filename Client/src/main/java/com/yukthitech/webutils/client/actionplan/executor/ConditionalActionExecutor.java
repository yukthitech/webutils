package com.yukthitech.webutils.client.actionplan.executor;

import com.yukthitech.webutils.client.actionplan.ActionPlanExecutionContext;
import com.yukthitech.webutils.common.action.IAgentAction;
import com.yukthitech.webutils.common.actionplan.ConditionalAction;

/**
 * Action executor for conditional actions.
 * @author akiran
 */
public class ConditionalActionExecutor implements IActionExecutor<ConditionalAction>
{
	@Override
	public void executeAction(ActionPlanExecutionContext context, ConditionalAction action) throws Exception
	{
		//evaluate condition, if condition is not met, move to next action
		if(!context.isConditionSatisfied(action.getCondition()))
		{
			context.executeNextAction(null);
			return;
		}
		
		//if condition is satisfied executed actual action
		for(IAgentAction subaction : action.getActions())
		{
			context.executeAction(subaction);
		}
	}
}
