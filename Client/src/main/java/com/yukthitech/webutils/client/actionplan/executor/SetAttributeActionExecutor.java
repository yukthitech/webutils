package com.yukthitech.webutils.client.actionplan.executor;

import com.yukthitech.webutils.client.actionplan.ActionPlanExecutionContext;
import com.yukthitech.webutils.common.actionplan.SetAttributeAction;

/**
 * Action executor for conditional actions.
 * @author akiran
 */
public class SetAttributeActionExecutor implements IActionExecutor<SetAttributeAction>
{
	@Override
	public void executeAction(ActionPlanExecutionContext context, SetAttributeAction action) throws Exception
	{
		//evaluate condition, if condition is not met, move to next action
		if(!context.isConditionSatisfied(action.getCondition()))
		{
			context.executeNextAction(null);
			return;
		}
		
		String value = context.processTemplate(action.getValue());
		context.executeNextAction(value);
	}
}
