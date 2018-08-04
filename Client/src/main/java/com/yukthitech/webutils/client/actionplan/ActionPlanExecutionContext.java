package com.yukthitech.webutils.client.actionplan;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.webutils.client.ClientContext;
import com.yukthitech.webutils.client.ClientControllerFactory;
import com.yukthitech.webutils.client.actionplan.executor.ActionCompletedEvent;
import com.yukthitech.webutils.client.actionplan.executor.IActionExecutor;
import com.yukthitech.webutils.client.actionplan.executor.IActionPlanExecutorCallback;
import com.yukthitech.webutils.common.action.IAgentAction;
import com.yukthitech.webutils.common.actionplan.ActionPlan;
import com.yukthitech.webutils.common.actionplan.ActionPlanStep;
import com.yukthitech.webutils.common.actionplan.StepCondition;

/**
 * Context of executor action which keeps track of context attributes and also actions execution sequence.
 * @author akiran
 */
public class ActionPlanExecutionContext
{
	private static Logger logger = LogManager.getLogger(ActionPlanExecutionContext.class);
	
	/**
	 * For parsing json.
	 */
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	/**
	 * Client context for execution of apis.
	 */
	private ClientContext clientContext;
	
	/**
	 * Client controller factory for fetching controllers.
	 */
	private ClientControllerFactory clientControllerFactory;
	
	/**
	 * For processing templates.
	 */
	private ITemplateProcessor templateProcessor;
	
	/**
	 * Action plan being executed.
	 */
	private ActionPlan actionPlan;
	
	/**
	 * Context to be used for evaluation.
	 */
	private Map<String, Object> context;
	
	/**
	 * Action type to executor mapping.
	 */
	private Map<Class<? extends IAgentAction>, IActionExecutor<?>> actionExecutors;

	/**
	 * Index of step being executed.
	 */
	private int stepIndex = 0;
	
	/**
	 * Step action to be executed.
	 */
	private int stepActionIndex = 0;
	
	/**
	 * Action which was currently executing.
	 */
	private IAgentAction currentAction;

	/**
	 * True if active step is evaluated.
	 */
	private boolean stepEvaluated = false;
	
	/**
	 * Callback to be called at end of execution.
	 */
	private IActionPlanExecutorCallback callback;
	
	/**
	 * Flag indicating if action plan execution is completed or not. 
	 */
	private boolean executionCompleted = false;
	
	/**
	 * Instantiates a new action plan execution context.
	 *
	 * @param templateProcessor the template processor
	 * @param actionPlan the action plan
	 * @param context the context
	 * @param actionExecutors the action executors
	 * @param clientControllerFactory the client controller factory
	 * @param callback the callback
	 */
	public ActionPlanExecutionContext(ITemplateProcessor templateProcessor, ActionPlan actionPlan, 
			Map<String, Object> context, Map<Class<? extends IAgentAction>, IActionExecutor<?>> actionExecutors,
			ClientControllerFactory clientControllerFactory,
			IActionPlanExecutorCallback callback)
	{
		this.clientContext = clientControllerFactory.getClientContext();
		this.templateProcessor = templateProcessor;
		this.actionPlan = actionPlan;
		this.context = context;
		this.actionExecutors = actionExecutors;
		this.clientControllerFactory = clientControllerFactory;
		this.callback = callback;
	}
	
	/**
	 * Gets the client controller factory for fetching controllers.
	 *
	 * @return the client controller factory for fetching controllers
	 */
	public ClientControllerFactory getClientControllerFactory()
	{
		return clientControllerFactory;
	}
	
	/**
	 * Evaluates specified template and returns the processed string.
	 * @param template template to process
	 * @return processed template result
	 */
	public String processTemplate(String template)
	{
		if(template == null)
		{
			return null;
		}
		
		return templateProcessor.processTemplate("Template", template, context);
	}
	
	/**
	 * Moves execution to next step.
	 */
	private boolean executeNextStep()
	{
		stepIndex++;
		stepActionIndex = 0;
		stepEvaluated = false;
		return executeNextAction(null);
	}
	
	/**
	 * Executes next action by adding specified result as result of current action.
	 *
	 * @param result the result of current action
	 * @return true, if next action was invoked successfully.
	 */
	public boolean executeNextAction(Object result)
	{
		if(executionCompleted)
		{
			return true;
		}
		
		List<ActionPlanStep> steps = actionPlan.getSteps();
		
		if(currentAction != null)
		{
			if(result != null)
			{
				logger.debug("For action '{}' setting action result as: {}", currentAction.getName(), result);
				context.put(currentAction.getName(), result);
			}
			
			if(callback != null)
			{
				ActionPlanStep activeStep = steps.get(stepIndex);
				callback.actionCompleted(new ActionCompletedEvent(activeStep.getName(), currentAction.getName()));
			}
			
			currentAction = null;
		}
		
		if(stepIndex >= steps.size())
		{
			if(callback != null)
			{
				callback.actionPlanExecuted(this.context);
			}
			
			executionCompleted = true;
			return true;
		}
		
		ActionPlanStep activeStep = steps.get(stepIndex);
		
		if(!stepEvaluated)
		{
			if(activeStep.getCondition() != null && !isConditionSatisfied(activeStep.getCondition()))
			{
				logger.debug("Skipping step '{}' as associated condition is not satisfied.", activeStep.getName());
				return executeNextStep();
			}
			else
			{
				logger.debug("Execution started for step: {}", activeStep.getName());
			}
		}
		
		//mark step as evaluated, so that step is not evaluated for every action.
		stepEvaluated = true;
		
		List<IAgentAction> actions = activeStep.getActions();
		
		// if actions are completed in current step
		//	move to next step and execute its first action
		if(stepActionIndex >= actions.size())
		{
			return executeNextStep();
		}
		
		IAgentAction action = actions.get(stepActionIndex);
		logger.debug("Under step '{}' executing action: {}", activeStep.getName(), action.getName());

		//set current action, so that the result is kept on context when next action is executed.
		currentAction = action;
		//move to next action index, so that in same thread if next action is executed
		stepActionIndex++;
		
		executeAction(currentAction);
		return true;
	}
	
	/**
	 * Finalizes the execution. If finalize action is specified the same will be executed.
	 * This will take execution to the end of action plan.
	 */
	public void finalizeExecution()
	{
		stepIndex = actionPlan.getSteps().size();
		
		if(actionPlan.getFinalAction() != null)
		{
			logger.debug("Executing finalization action: {}", actionPlan.getFinalAction().getName());
			
			executeAction(actionPlan.getFinalAction());
		}
		else
		{
			if(callback != null)
			{
				callback.actionPlanExecuted(this.context);
			}
		}
	}
	
	/**
	 * Executes the specified action.
	 * @param action action to execute.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void executeAction(IAgentAction action)
	{
		IActionExecutor<IAgentAction> executor = (IActionExecutor) actionExecutors.get(action.getClass());
		
		if(executor == null)
		{
			throw new InvalidStateException("No executor found for action type: {}", action.getClass().getName());
		}
	
		try
		{
			executor.executeAction(this, action);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while execution of action: {}", action.getName(), ex);
		}
	}
	
	/**
	 * Checks if specified condition is satisfied as per current context.
	 * @param condition condition to check
	 * @return true if condition is satisifed.
	 */
	public boolean isConditionSatisfied(StepCondition condition)
	{
		if(condition == null)
		{
			return true;
		}
		
		String condValue = this.templateProcessor.processTemplate("Condition", condition.getExpression(), context);
		
		logger.trace("Condition expression '{}' is evaluated as: {}", condition.getExpression(), condValue);
		
		boolean conditionSatisfied = false;
		
		if(StringUtils.isNotBlank(condition.getValue()))
		{
			conditionSatisfied = condition.getValue().equals(condValue);
		}
		else if(StringUtils.isNotBlank(condition.getNotValue()))
		{
			conditionSatisfied = !condition.getNotValue().equals(condValue);
		}
		
		//if main condition is satisfied
		if(conditionSatisfied)
		{
			//if 'and' condition is satisfied then evaluate the and
			if(condition.getAnd() != null)
			{
				return isConditionSatisfied(condition.getAnd());
			}
			
			return conditionSatisfied;
		}
		
		//if main condition is not satisfied but has 'or', evaluate or condition
		if(condition.getOr() != null)
		{
			return isConditionSatisfied(condition.getOr());
		}
		
		return false;
	}
	
	/**
	 * Gets the context attribute.
	 *
	 * @param name the name
	 * @return the context attribute
	 */
	public Object getContextAttribute(String name)
	{
		return context.get(name);
	}
	
	/**
	 * Sets the context attribute with specified name and value.
	 * @param name name of attr
	 * @param value value of attr
	 */
	public void setContextAttribute(String name, Object value)
	{
		context.put(name, value);
	}
	
	/**
	 * Gets the client context for execution of apis.
	 *
	 * @return the client context for execution of apis
	 */
	public ClientContext getClientContext()
	{
		return clientContext;
	}
	
	/**
	 * Parses the json to specified type.
	 * @param json json to parse
	 * @param type expected type
	 * @param <T> expected object type
	 * @return parsed object
	 */
	public <T> T parseJson(String json, Class<T> type)
	{
		try
		{
			return objectMapper.readValue(json, type);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while parsing json.", ex);
		}
	}

	/**
	 * Parses the json to specified type.
	 * @param json json to parse
	 * @param typeRef expected type
	 * @param <T> expected object type
	 * @return parsed object
	 */
	public <T> T parseJson(String json, TypeReference<T> typeRef)
	{
		try
		{
			return objectMapper.readValue(json, typeRef);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while parsing json.", ex);
		}
	}
}
