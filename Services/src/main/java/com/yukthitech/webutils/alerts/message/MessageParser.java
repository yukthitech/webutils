package com.yukthitech.webutils.alerts.message;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

/**
 * Message parsing for parsing incoming messages based on rules.
 * @author akiran
 */
@Service
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
	public boolean loadContext(String patternStr, String content, Map<String, Object> context)
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
}
