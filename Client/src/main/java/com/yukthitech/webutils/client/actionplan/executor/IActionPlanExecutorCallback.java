package com.yukthitech.webutils.client.actionplan.executor;

import java.util.Map;

/**
 * Callback interface that can be used to invoke functionality once
 * action plan is executed.
 * @author akiran
 */
public interface IActionPlanExecutorCallback
{
	/**
	 * Called when an action execution is completed.
	 * @param event details of action completed
	 */
	public void actionCompleted(ActionCompletedEvent event);
	
	/**
	 * Called by action plan executor with current context once action plan is executed successfully.
	 * @param context context used for execution.
	 */
	public void actionPlanExecuted(Map<String, Object> context);
}
