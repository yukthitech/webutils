package com.yukthitech.webutils.client.actionplan;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.yukthitech.webutils.client.ClientControllerFactory;
import com.yukthitech.webutils.client.actionplan.executor.SendAlertExecutor;
import com.yukthitech.webutils.client.actionplan.executor.ApiActionExecutor;
import com.yukthitech.webutils.client.actionplan.executor.ConditionalActionExecutor;
import com.yukthitech.webutils.client.actionplan.executor.FinalizeExecutionActionExecutor;
import com.yukthitech.webutils.client.actionplan.executor.IActionExecutor;
import com.yukthitech.webutils.client.actionplan.executor.IActionPlanExecutorCallback;
import com.yukthitech.webutils.client.actionplan.executor.SetAttributeActionExecutor;
import com.yukthitech.webutils.common.action.SendAlertAction;
import com.yukthitech.webutils.common.action.ApiAgentAction;
import com.yukthitech.webutils.common.action.FinalizeExecutionAction;
import com.yukthitech.webutils.common.action.IAgentAction;
import com.yukthitech.webutils.common.actionplan.ActionPlan;
import com.yukthitech.webutils.common.actionplan.ConditionalAction;
import com.yukthitech.webutils.common.actionplan.SetAttributeAction;

/**
 * Executor for action plan.
 * @author akiran
 */
public class ActionPlanExecutor
{
	/**
	 * Action type to executor mapping.
	 */
	private static Map<Class<? extends IAgentAction>, IActionExecutor<?>> actionExecutors = new HashMap<>();
	
	static
	{
		actionExecutors.put(ApiAgentAction.class, new ApiActionExecutor());
		actionExecutors.put(SendAlertAction.class, new SendAlertExecutor());
		actionExecutors.put(ConditionalAction.class, new ConditionalActionExecutor());
		actionExecutors.put(SetAttributeAction.class, new SetAttributeActionExecutor());
		actionExecutors.put(FinalizeExecutionAction.class, new FinalizeExecutionActionExecutor());
	}
	
	/**
	 * Execute specified action plan with specified context.
	 *
	 * @param clientControllerFactory the client controller factory
	 * @param actionPlan the action plan
	 * @param context the context
	 * @param templateProcessor the template processor to process params and entity templates.
	 */
	public static void executeActionPlan(ClientControllerFactory clientControllerFactory,
			ActionPlan actionPlan, Map<String, Object> context, ITemplateProcessor templateProcessor, IActionPlanExecutorCallback callback)
	{
		ActionPlanExecutionContext actionPlanExecutionContext = new ActionPlanExecutionContext(templateProcessor, 
				actionPlan, context, 
				Collections.unmodifiableMap(actionExecutors),
				clientControllerFactory,
				callback
				);
		actionPlanExecutionContext.executeNextAction(null);
	}
	
	/**
	 * Registers specified action type to executor mapping.
	 * @param actionType action type
	 * @param executor executor
	 * @param <A> action type
	 */
	public static <A extends IAgentAction> void regiser(Class<A> actionType, IActionExecutor<A> executor)
	{
		actionExecutors.put(actionType, executor);
	}
}
