package com.yukthitech.webutils.common.alerts;

import com.yukthitech.persistence.repository.annotations.Field;
import com.yukthitech.webutils.common.BaseModel;
import com.yukthitech.webutils.common.annotations.Model;

/**
 * Indicates basic details of rule which is used to convert message (sms or mail) into an alert.
 * @author akiran
 */
@Model
public class BasicMessageParsingRuleModel extends BaseModel
{
	/**
	 * From address pattern to be used.
	 */
	@Field("fromAddressPattern")
	private String fromAddressPattern;
	
	/**
	 * Filter pattern only when matched, the target message will be processed to alert.
	 */
	@Field("messageFilterPattern")
	private String messageFilterPattern;

	/**
	 * Gets the from address pattern to be used.
	 *
	 * @return the from address pattern to be used
	 */
	public String getFromAddressPattern()
	{
		return fromAddressPattern;
	}

	/**
	 * Sets the from address pattern to be used.
	 *
	 * @param fromAddressPattern the new from address pattern to be used
	 */
	public void setFromAddressPattern(String fromAddressPattern)
	{
		this.fromAddressPattern = fromAddressPattern;
	}

	/**
	 * Gets the filter pattern only when matched, the target message will be processed to alert.
	 *
	 * @return the filter pattern only when matched, the target message will be processed to alert
	 */
	public String getMessageFilterPattern()
	{
		return messageFilterPattern;
	}

	/**
	 * Sets the filter pattern only when matched, the target message will be processed to alert.
	 *
	 * @param messageFilterPattern the new filter pattern only when matched, the target message will be processed to alert
	 */
	public void setMessageFilterPattern(String messageFilterPattern)
	{
		this.messageFilterPattern = messageFilterPattern;
	}
}
