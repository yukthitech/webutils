package com.yukthitech.webutils.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleCacheManager;

/**
 * Customized spring cache manager.
 * @author akiran
 */
public class WebutilsSpringCacheManager extends SimpleCacheManager
{
	@Override
	protected Cache getMissingCache(String name) 
	{
		return new WebutilsCache(name);
	}
}
