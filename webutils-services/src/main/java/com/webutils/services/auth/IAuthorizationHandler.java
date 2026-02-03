package com.webutils.services.auth;

import org.springframework.web.method.HandlerMethod;

import com.webutils.services.common.UnauthenticatedRequestException;

public interface IAuthorizationHandler
{
	public void checkAuthorization(HandlerMethod handlerMethod) throws UnauthenticatedRequestException;
}
