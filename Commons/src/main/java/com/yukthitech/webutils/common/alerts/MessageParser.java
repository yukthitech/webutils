package com.yukthitech.webutils.common.alerts;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.ConvertUtils;
import com.yukthitech.utils.beans.BeanInfo;
import com.yukthitech.utils.beans.PropertyInfo;
import com.yukthitech.utils.beans.PropertyMapper;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Message parsing for parsing incoming messages based on rules.
 * @author akiran
 */
public class MessageParser
{
	/**
	 * Used to extract groups names from other patterns.
	 */
	private static final Pattern GROUP_EXTRACT_PATTERN = Pattern.compile("\\(\\?\\<([a-zA-Z][a-zA-Z0-9]*)\\>");
	
	/**
	 * Encapsulation of pattern and groups.
	 * @author akiran
	 */
	private static class PatternWithGroups
	{
		/**
		 * Pattern to be used.
		 */
		private Pattern pattern;
		
		/**
		 * Groups names in pattern.
		 */
		private Set<String> groups;

		/**
		 * Instantiates a new pattern with groups.
		 *
		 * @param pattern the pattern
		 * @param groups the groups
		 */
		public PatternWithGroups(Pattern pattern, Set<String> groups)
		{
			this.pattern = pattern;
			this.groups = groups;
		}
	}
	
	/**
	 * Map to cache parsed patterns.
	 */
	private Map<String, PatternWithGroups> patternCache = new HashMap<>();
	
	/**
	 * Parses input string into pattern and group names. This method uses local cache
	 * for performance sake.
	 * 
	 * @param patternStr pattern string to parse
	 * @return parsed pattern with group names
	 */
	private synchronized PatternWithGroups toPattern(String patternStr)
	{
		PatternWithGroups patternWithGroups = patternCache.get(patternStr);

		if(patternWithGroups != null)
		{
			return patternWithGroups;
		}
		
		Pattern pattern = Pattern.compile(patternStr);
		Set<String> groups = new HashSet<>();
		
		Matcher matcher = GROUP_EXTRACT_PATTERN.matcher(patternStr);
		
		while(matcher.find())
		{
			groups.add(matcher.group(1));
		}
		
		patternWithGroups = new PatternWithGroups(pattern, groups);
		patternCache.put(patternStr, patternWithGroups);

		return patternWithGroups;
	}
	
	/**
	 * Checks if specified content is having specified pattern string. If found, extracts the 
	 * group values and adds to context.
	 * 
	 * @param patternStr pattern to find
	 * @param content content in which pattern needs to be searched.
	 * @param context context to which groups needs to be added
	 * @return true if pattern is matched.
	 */
	private boolean isMatching(String patternStr, String content, Map<String, String> context)
	{
		PatternWithGroups patternWithGroups = toPattern(patternStr);
		Matcher matcher = patternWithGroups.pattern.matcher(content);
		
		if(!matcher.find())
		{
			return false;
		}

		for(String grp : patternWithGroups.groups)
		{
			context.put(grp, matcher.group(grp));
		}
		
		return true;
	}
	
	/**
	 * Used to check if input message is matching with specified rule. If matching, context will be populated
	 * with attributes from patterns.
	 * @param rule rule to match with
	 * @param from from address or number
	 * @param message message body
	 * @param context context to be populated
	 * @return true if matched
	 */
	private boolean isMatching(MessageParsingRuleModel rule, String from, String message, Map<String, String> context)
	{
		if(rule.getFromAddressPattern() != null)
		{
			if(!isMatching(rule.getFromAddressPattern(), from, context))
			{
				return false;
			}
		}
		
		if(rule.getMessageFilterPattern() != null)
		{
			if(!isMatching(rule.getMessageFilterPattern(), message, context))
			{
				return false;
			}
		}
		
		//simply extract context variables from other patterns
		
		if(rule.getMessagePatterns() != null)
		{
			for(String msgPattern : rule.getMessagePatterns())
			{
				isMatching(msgPattern, message, context);
			}
		}
		
		return true;
	}
	
	/**
	 * Parses the alert details from given rule and context.
	 *
	 * @param source the source of the alert being generated
	 * @param rule the rule to be used for building
	 * @param context the context to be used for building
	 * @return constructed alert details
	 */
	private AlertDetails parseAlertDetails(String source, MessageParsingRuleModel rule, Map<String, String> context)
	{
		AlertDetails alertDetails = new AlertDetails();
		
		if(rule.getAlertBeanType() != null)
		{
			Object alertBean = null;
			BeanInfo beanInfo = null;
			
			try
			{
				Class<?> type = Class.forName(rule.getAlertBeanType());
				alertBean = type.newInstance();
				
				beanInfo = PropertyMapper.getBeanInfo(type);
			}catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while creating alert bean of type: {}", rule.getAlertBeanType(), ex);
			}
			
			for(String key : context.keySet())
			{
				PropertyInfo prop = beanInfo.getProperty(key);
				
				if(prop == null)
				{
					continue;
				}
				
				Object value = ConvertUtils.convert(context.get(key), prop.getProperty().getType());
				prop.getProperty().setValue(alertBean, value);
			}
			
			alertDetails.setData(alertBean);
		}
		
		String title = CommonUtils.replaceExpressions(context, rule.getTitle(), null);
		String mssg = CommonUtils.replaceExpressions(context, rule.getMessage(), null);
		
		alertDetails.setAlertType(rule.getAlertType());
		alertDetails.setTitle(title);
		alertDetails.setMessage(mssg);
		alertDetails.setSource(source);
		
		return alertDetails;
	}
	
	/**
	 * Parses the message into alert details. For each matching rule, corresponding alert details would be generated.
	 *
	 * @param source the source for which alerts are being built
	 * @param rules the rules to be used for matching
	 * @param from source of message
	 * @param message message to be used
	 * @return Rule to alert details to be constructed.
	 */
	public Map<MessageParsingRuleModel, AlertDetails> parseMessage(String source, Collection<MessageParsingRuleModel> rules, String from, String message)
	{
		Map<MessageParsingRuleModel, AlertDetails> resMap = new HashMap<>();
		
		for(MessageParsingRuleModel rule : rules)
		{
			Map<String, String> context = new HashMap<>();
			
			if(!isMatching(rule, from, message, context))
			{
				continue;
			}
			
			context.put("_from", from);
			context.put("_message", message);
			
			AlertDetails alertDetails = parseAlertDetails(source, rule, context);
			resMap.put(rule, alertDetails);
		}
		
		return resMap;
	}
}
