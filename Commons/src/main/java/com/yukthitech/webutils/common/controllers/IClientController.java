package com.yukthitech.webutils.common.controllers;

import com.yukthitech.webutils.common.client.IRequestCustomizer;

/**
 * Contains common controller methods required by client.
 * @param <C> Expected to pass current controller
 * @author akiran
 */
public interface IClientController<C extends IClientController<C>>
{
	/**
	 * Sets the request customizer on the underlying client context. 
	 * @param customizer Customized to customize request.
	 * @return Returns current controller instance for nested call.
	 */
	public default C setRequestCustomizer(IRequestCustomizer customizer)
	{
		return null;
	}
}
