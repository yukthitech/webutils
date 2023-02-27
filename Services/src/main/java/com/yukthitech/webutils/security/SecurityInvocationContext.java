package com.yukthitech.webutils.security;

import java.lang.reflect.Method;

import com.yukthitech.webutils.common.models.ActionModel;

/**
 * Encapsulation of data used to check for authorization. 
 * @author akiran
 */
public class SecurityInvocationContext
{
	/**
	 * Target class on which method being invoked.
	 */
	private Class<?> target;
	
	/**
	 * Method being invoked.
	 */
	private Method method;
	
	/**
	 * Action details of the method being invoked, if any.
	 */
	private ActionModel action;
	
	/**
	 * Current request, for which security check is being made.
	 */
	private Object request;

	/**
	 * Instantiates a new security invocation context.
	 *
	 * @param target the controller
	 * @param method the method
	 * @param action the action
	 */
	public SecurityInvocationContext(Class<?> target, Method method, ActionModel action, Object request)
	{
		this.target = target;
		this.method = method;
		this.action = action;
		this.request = request;
	}

	/**
	 * Gets the target class on which method being invoked.
	 *
	 * @return the target class on which method being invoked
	 */
	public Class<?> getTarget()
	{
		return target;
	}

	/**
	 * Gets the method being invoked.
	 *
	 * @return the method being invoked
	 */
	public Method getMethod()
	{
		return method;
	}

	/**
	 * Gets the action details of the method being invoked, if any.
	 *
	 * @return the action details of the method being invoked, if any
	 */
	public ActionModel getAction()
	{
		return action;
	}
	
	/**
	 * Gets the current request, for which security check is being made.
	 *
	 * @return the current request, for which security check is being made
	 */
	public Object getRequest()
	{
		return request;
	}
}
