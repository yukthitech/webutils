package com.yukthitech.webutils.common.action;

/**
 * This action will enable to make a call and on completion of call
 * displays the form. And on submission of form specified event will be generated.
 * @author akiran
 */
public class CallAndFillFormAction extends FillFormAction
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
