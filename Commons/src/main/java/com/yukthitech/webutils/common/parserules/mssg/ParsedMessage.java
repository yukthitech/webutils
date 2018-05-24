package com.yukthitech.webutils.common.parserules.mssg;

import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.yukthitech.utils.ObjectWrapper;
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
	 * Type of contact from which message is received.
	 */
	private String fromType;
	
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
	 * Gets the type of contact from which message is received.
	 *
	 * @return the type of contact from which message is received
	 */
	public String getFromType()
	{
		return fromType;
	}

	/**
	 * Sets the type of contact from which message is received.
	 *
	 * @param fromType the new type of contact from which message is received
	 */
	public void setFromType(String fromType)
	{
		this.fromType = fromType;
	}

	/**
	 * Checks if the current message is matching with specified rule.
	 * @param basicRule rule to be matched with
	 * @param matchError Wrapper which will hold approp error message if current message is not matching with specified rule. 
	 * @return true if message is matched
	 */
	public boolean isMatchingWith(BasicMessageParseRuleModel basicRule, ObjectWrapper<String> matchError)
	{
		if(StringUtils.isNotBlank(basicRule.getFromType()))
		{
			matchError.setValue(String.format("Input from type '%s' is not matching with rule's from type '%s'", fromType, basicRule.getFromType()));
			return basicRule.getFromType().equals(fromType);
		}

		if(StringUtils.isNotBlank(basicRule.getFromAddressPattern()))
		{
			if(StringUtils.isBlank(from))
			{
				matchError.setValue(String.format("Input from is blank which is not matching with rule's from pattern '%s'", basicRule.getFromAddressPattern()));
				return false;
			}
			
			Pattern fromPattern = Pattern.compile(basicRule.getFromAddressPattern());
			
			if(!fromPattern.matcher(from).find())
			{
				matchError.setValue(String.format("Input from '%s' is not matching with rule's from pattern '%s'", from, basicRule.getFromAddressPattern()));
				return false;
			}
		}

		if(CollectionUtils.isNotEmpty(basicRule.getMessageFilterPatterns()))
		{
			if(StringUtils.isBlank(message))
			{
				matchError.setValue(String.format("Rule's message pattern '%s' is not matching input blank message", basicRule.getMessageFilterPatterns()));
				return false;
			}
			
			for(String messgPtrn : basicRule.getMessageFilterPatterns())
			{
				Pattern msgPattern = Pattern.compile(messgPtrn);
				
				if(!msgPattern.matcher(message).find())
				{
					matchError.setValue(String.format("Rule's message pattern '%s' is not matching input message: %s", messgPtrn, message));
					return false;
				}
			}
		}
		
		return true;
	}
}
