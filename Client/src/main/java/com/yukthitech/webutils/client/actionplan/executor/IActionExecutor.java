package com.yukthitech.webutils.client.actionplan.executor;

import com.yukthitech.webutils.client.actionplan.ActionPlanExecutionContext;
import com.yukthitech.webutils.common.action.IAgentAction;

/**
 * Abstraction of action executor.
 * @author akiran
 * @param <T> type of action executable by this executor
 */
public interface IActionExecutor<T extends IAgentAction>
{
	/**
	 * Executes specified action.
	 *
	 * @param context the context
	 * @param action action to execute
	 */
	public void executeAction(ActionPlanExecutionContext context, T action) throws Exception;
}
