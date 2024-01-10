package com.yukthitech.webutils.common.actionplan;

import com.yukthitech.webutils.common.action.AbstractAgentAction;
import com.yukthitech.webutils.common.action.AlertAction;
import com.yukthitech.webutils.common.action.ApiAgentAction;
import com.yukthitech.webutils.common.action.CallAction;
import com.yukthitech.webutils.common.action.CallAndFillFormAction;
import com.yukthitech.webutils.common.action.FailAction;
import com.yukthitech.webutils.common.action.FillFormAction;
import com.yukthitech.webutils.common.action.FinalizeExecutionAction;
import com.yukthitech.webutils.common.action.IAgentAction;
import com.yukthitech.webutils.common.action.InputAction;
import com.yukthitech.webutils.common.action.SendAlertAction;
import com.yukthitech.webutils.common.action.SendInputAlertAction;
import com.yukthitech.webutils.common.action.SendMailAction;
import com.yukthitech.webutils.common.action.ViewAndConfirmAction;
import com.yukthitech.webutils.common.action.mobile.EditAndSendSmsAction;
import com.yukthitech.webutils.common.action.mobile.SendSmsAction;

/**
 * Base class to support different type of actions by implementing single action method.
 * @author akiran
 */
public abstract class AbstractMultiActionSupport extends AbstractAgentAction
{
	/**
	 * Adds action to current step.
	 * @param action action to add.
	 */
	public void addInvokeApi(ApiAgentAction action)
	{
		addAction(action);
	}
	
	/**
	 * Adds action to current step.
	 * @param action action to add.
	 */
	public void addSendAlert(SendAlertAction action)
	{
		addAction(action);
	}
	
	/**
	 * Adds action to current step.
	 * @param action action to add.
	 */
	public void addCallAndFillForm(CallAndFillFormAction action)
	{
		addAction(action);
	}
	
	/**
	 * Adds action to current step.
	 * @param action action to add.
	 */
	public void addFillForm(FillFormAction action)
	{
		addAction(action);
	}
	
	/**
	 * Adds action to current step.
	 * @param action action to add.
	 */
	public void addViewAndConfirm(ViewAndConfirmAction action)
	{
		addAction(action);
	}
	
	/**
	 * Adds action to current step.
	 * @param action action to add.
	 */
	public void addConditionAction(ConditionalAction action)
	{
		addAction(action);
	}

	/**
	 * Adds action to current step.
	 * @param action action to add.
	 */
	public void addSetAttribute(SetAttributeAction action)
	{
		addAction(action);
	}
	
	/**
	 * Adds action to current step.
	 * @param action action to add.
	 */
	public void addFinalizeExecution(FinalizeExecutionAction action)
	{
		addAction(action);
	}
	
	/**
	 * Adds the send sms.
	 *
	 * @param action the action
	 */
	public void addSendSms(SendSmsAction action)
	{
		addAction(action);
	}

	/**
	 * Adds the send sms.
	 *
	 * @param action the action
	 */
	public void addEditAndSendSms(EditAndSendSmsAction action)
	{
		addAction(action);
	}

	/**
	 * Adds the alert.
	 *
	 * @param action the action
	 */
	public void addAlert(AlertAction action)
	{
		addAction(action);
	}

	public void addInput(InputAction action)
	{
		addAction(action);
	}
	
	public void addFail(FailAction action)
	{
		addAction(action);
	}

	/**
	 * Adds the call.
	 *
	 * @param action the action
	 */
	public void addCall(CallAction action)
	{
		addAction(action);
	}

	/**
	 * Adds the send input alert.
	 *
	 * @param action the action
	 */
	public void addSendInputAlert(SendInputAlertAction action)
	{
		addAction(action);
	}
	
	public void addSendMail(SendMailAction action)
	{
		addAction(action);
	}

	/**
	 * Adds action to current object.
	 * @param action action to add.
	 */
	public abstract void addAction(IAgentAction action);
}
