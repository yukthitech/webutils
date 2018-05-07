package com.yukthitech.webutils.alerts.agent.system;

/**
 * Context that will be sent alert processor.
 * @author akiran
 */
public interface ISystemAlertProcessorContext
{
	/**
	 * Sets attribute with specified name and value.
	 * @param name name of attr
	 * @param value value of attr
	 */
	public void setAttribute(String name, Object value);
	
	/**
	 * Fetches attribute with specified name.
	 * @param name name of attr to fetch
	 * @return value of attr.
	 */
	public Object getAttribute(String name);
	
	/**
	 * Removes specified attribute.
	 * @param name name of attribute to remove.
	 */
	public void removeAttribte(String name);
}
