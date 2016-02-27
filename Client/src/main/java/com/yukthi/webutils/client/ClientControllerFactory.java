package com.yukthi.webutils.client;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import com.yukthi.utils.exceptions.InvalidArgumentException;

public class ClientControllerFactory
{
	private ClientContext clientContext;
	
	private Map<Class<?>, Object> typeToController = new HashMap<>();
	
	public ClientControllerFactory(ClientContext clientContext)
	{
		this.clientContext = clientContext;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getController(Class<T> controllerType)
	{
		if(!controllerType.isInterface())
		{
			throw new InvalidArgumentException("Non interface type specified for controller type - {}", controllerType.getName());
		}
		
		Object controller = typeToController.get(controllerType);
		
		if(controller != null)
		{
			return (T) controller;
		}
		
		ControllerProxy proxy = new ControllerProxy(clientContext, controllerType);
		controller = Proxy.newProxyInstance(ClientControllerFactory.class.getClassLoader(), new Class<?>[] {controllerType}, proxy);

		typeToController.put(controllerType, controller);
		return (T) controller;
	}
}
