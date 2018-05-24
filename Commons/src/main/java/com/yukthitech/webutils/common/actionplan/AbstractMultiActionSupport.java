package com.yukthitech.webutils.common.actionplan;

import com.yukthitech.webutils.common.action.AlertAgentAction;
import com.yukthitech.webutils.common.action.ApiAgentAction;
import com.yukthitech.webutils.common.action.CallAndFillFormAction;
import com.yukthitech.webutils.common.action.FillFormAction;
import com.yukthitech.webutils.common.action.IAgentAction;
import com.yukthitech.webutils.common.action.ViewAndConfirmAction;

/**
 * Base class to support different type of actions by implementing single action method.
 * @author akiran
 */
public abstract class AbstractMultiActionSupport
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
	public void addSendAlert(AlertAgentAction action)
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
	 * Adds action to current object.
	 * @param action action to add.
	 */
	public abstract void addAction(IAgentAction action);
}
