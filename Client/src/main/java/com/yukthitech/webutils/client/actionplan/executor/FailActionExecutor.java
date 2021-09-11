package com.yukthitech.webutils.client.actionplan.executor;

import com.yukthitech.webutils.client.actionplan.ActionPlanExecutionContext;
import com.yukthitech.webutils.common.action.ActionPlanFailedException;
import com.yukthitech.webutils.common.action.FailAction;

/**
 * Alert action executor.
 * @author akiran
 */
public class FailActionExecutor implements IActionExecutor<FailAction>
{
	@Override
	public void executeAction(ActionPlanExecutionContext context, FailAction action) throws Exception
	{
		String message = context.processTemplate(action.getMessage());
		throw new ActionPlanFailedException(message);
	}
}
