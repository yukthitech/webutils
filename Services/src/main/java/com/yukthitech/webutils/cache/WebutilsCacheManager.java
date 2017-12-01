package com.yukthitech.webutils.cache;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * Cache manager for managing caches.
 * TODO: Use ignite here
 * @author akiran
 */
@Component
public class WebutilsCacheManager
{
	/**
	 * Map to manage cache.
	 */
	private Map<String, Object> cacheMap = new HashMap<>();
	
	/**
	 * Sets specified value on cache with specified key.
	 * @param key Key for cache.
	 * @param value Value to be set.
	 */
	public void set(String key, Object value)
	{
		cacheMap.put(key, value);
	}
	
	/**
	 * Gets the value of specified key.
	 * @param key Key for which value needs to be obtained.
	 * @return Matching value.
	 */
	public Object get(String key)
	{
		return cacheMap.get(key);
	}
	
	/**
	 * Removes the specified key from cache.
	 * @param key Key to be removed.
	 * @return old vlaue
	 */
	public Object remove(String key)
	{
		return cacheMap.remove(key);
	}
}
