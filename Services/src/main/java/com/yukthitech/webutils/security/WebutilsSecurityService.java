package com.yukthitech.webutils.security;

import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.yukthitech.webutils.common.models.ActionModel;
import com.yukthitech.webutils.services.ActionsService;

/**
 * Basic utility methods used by security module.
 * @author akiran
 */
@Service
public class WebutilsSecurityService
{
	/**
	 * Used to fetch action details of the controller method being invoked.
	 * Which in turn is used for authorization.
	 */
	@Lazy
	@Autowired
	private ActionsService actionService;

	/**
	 * Creates security invocation context for specified invocation.
	 *
	 * @param controllerType the controller type
	 * @param method the method
	 * @return the security invocation context
	 */
	public SecurityInvocationContext newSecurityInvocationContext(Class<?> controllerType, Method method)
	{
		ActionModel action = actionService.getActionDetails(controllerType, method);
		SecurityInvocationContext context = new SecurityInvocationContext(controllerType, method, action);

		return context;
	}
}
