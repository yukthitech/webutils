package com.yukthitech.webutils.common.action;

/**
 * This action will enable to make a call.
 * @author akiran
 */
public class CallAction extends AbstractAgentAction
{
	/**
	 * Number to be called.
	 */
	private String number;

	/**
	 * Gets the number to be called.
	 *
	 * @return the number to be called
	 */
	public String getNumber()
	{
		return number;
	}

	/**
	 * Sets the number to be called.
	 *
	 * @param number the new number to be called
	 */
	public void setNumber(String number)
	{
		this.number = number;
	}
}
