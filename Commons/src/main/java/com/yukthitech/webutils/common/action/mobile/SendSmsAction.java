package com.yukthitech.webutils.common.action.mobile;

import com.yukthitech.webutils.common.action.AbstractAgentAction;
import com.yukthitech.webutils.common.action.IAgentAction;

/**
 * Sends sms message to the target user.
 * @author akiran
 */
public class SendSmsAction extends AbstractAgentAction implements IAgentAction
{
	/**
	 * Sms message being sent.
	 */
	private String message;
	
	/**
	 * Number to which sms message needs to be sent.
	 */
	private String number;
	
	/**
	 * Gets the sms message being sent.
	 *
	 * @return the sms message being sent
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * Sets the sms message being sent.
	 *
	 * @param message the new sms message being sent
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}

	/**
	 * Gets the number to which sms message needs to be sent.
	 *
	 * @return the number to which sms message needs to be sent
	 */
	public String getNumber()
	{
		return number;
	}

	/**
	 * Sets the number to which sms message needs to be sent.
	 *
	 * @param number the new number to which sms message needs to be sent
	 */
	public void setNumber(String number)
	{
		this.number = number;
	}
}
