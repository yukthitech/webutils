package com.yukthitech.webutils.common.action;

/**
 * Abstraction of action.
 * @author akiran
 */
public interface IAgentAction
{
	/**
	 * Name of the action.
	 * @return action name.
	 */
	public String getName();
	
	/**
	 * Label to be used for action.
	 * @return label
	 */
	public String getLabel();
}
