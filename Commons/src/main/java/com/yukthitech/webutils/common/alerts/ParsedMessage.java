package com.yukthitech.webutils.common.alerts;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.validation.annotations.MaxLen;
import com.yukthitech.validation.annotations.NotEmpty;
import com.yukthitech.validation.annotations.Required;
import com.yukthitech.webutils.common.annotations.Model;

/**
 * Represents message that is parsed according to parsing rules.
 * @author akiran
 */
@Model
public class ParsedMessage
{
	/**
	 * Source of message.
	 */
	@Required
	@NotEmpty
	@MaxLen(100)
	private String from;
	
	/**
	 * Message obtained.
	 */
	@Required
	@NotEmpty
	@MaxLen(1000)
	private String message;
	
	/**
	 * Title of message. Useful while parsing mail.
	 */
	@MaxLen(100)
	private String title;

	/**
	 * Gets the source of message.
	 *
	 * @return the source of message
	 */
	public String getFrom()
	{
		return from;
	}

	/**
	 * Sets the source of message.
	 *
	 * @param from the new source of message
	 */
	public void setFrom(String from)
	{
		this.from = from;
	}

	/**
	 * Gets the message obtained.
	 *
	 * @return the message obtained
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * Sets the message obtained.
	 *
	 * @param message the new message obtained
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}

	/**
	 * Gets the title of message. Useful while parsing mail.
	 *
	 * @return the title of message
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * Sets the title of message. Useful while parsing mail.
	 *
	 * @param title the new title of message
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	/**
	 * Checkes if the current message is matching with specified rule.
	 * @param basicRule rule to be matched with
	 * @return true if message is matched
	 */
	public boolean isMatchingWith(BasicMessageParsingRuleModel basicRule)
	{
		if(StringUtils.isNotBlank(from) && StringUtils.isNotBlank(basicRule.getFromAddressPattern()))
		{
			Pattern fromPattern = Pattern.compile(basicRule.getFromAddressPattern());
			
			if(!fromPattern.matcher(from).matches())
			{
				return false;
			}
		}

		if(StringUtils.isNotBlank(message) && StringUtils.isNotBlank(basicRule.getMessageFilterPattern()))
		{
			Pattern msgPattern = Pattern.compile(basicRule.getMessageFilterPattern());
			
			if(!msgPattern.matcher(message).matches())
			{
				return false;
			}
		}
		
		return true;
	}
}
