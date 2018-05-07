package com.yukthitech.webutils.cache;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleCacheManager;

/**
 * Customized spring cache manager.
 * @author akiran
 */
public class WebutilsSpringCacheManager extends SimpleCacheManager
{
	/**
	 * Pattern used to find the lru embedded with in cache name.
	 */
	public static final Pattern CACHE_NAME_WITH_LRU = Pattern.compile("\\w+\\[(\\d+)\\]");
	
	/**
	 * Cache lru size to be used.
	 */
	@Value("${webutils.cache.lruSize:20}")
	private int cacheSize;
	
	@Override
	protected Cache getMissingCache(String name) 
	{
		int cacheSize = this.cacheSize;
		Matcher matcher = CACHE_NAME_WITH_LRU.matcher(name);
		
		if(matcher.matches())
		{
			cacheSize = Integer.parseInt(matcher.group(1));
		}
		
		return new WebutilsCache(name, cacheSize);
	}
}
