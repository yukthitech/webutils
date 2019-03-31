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
	
	/**
	 * Fetches the type name of this action which will help non-java clients
	 * to understand what action is expected to be taken.
	 * @return type name of this action.
	 */
	public String getType();
}
