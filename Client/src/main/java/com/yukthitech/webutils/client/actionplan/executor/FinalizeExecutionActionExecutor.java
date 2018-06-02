package com.yukthitech.webutils.client.actionplan.executor;

import com.yukthitech.webutils.client.actionplan.ActionPlanExecutionContext;
import com.yukthitech.webutils.common.action.FinalizeExecutionAction;

/**
 * Action to finalize the action plan execution.
 * @author akiran
 */
public class FinalizeExecutionActionExecutor implements IActionExecutor<FinalizeExecutionAction>
{
	@Override
	public void executeAction(ActionPlanExecutionContext context, FinalizeExecutionAction action) throws Exception
	{
		context.finalizeExecution();
	}
}
