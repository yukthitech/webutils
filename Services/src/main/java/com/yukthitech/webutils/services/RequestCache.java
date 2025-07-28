package com.yukthitech.webutils.services;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Service;

/**
 * Used to cache the data on current request thread.
 */
@Service
public class RequestCache
{
	private ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<>();
	
	/**
	 * Fetches the value from the cache. If does not exist, uses supplier to get the value.
	 * 
	 * @param key
	 * @param supplier
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String key, Function<String, T> supplier)
	{
		Map<String, Object> cacheMap = threadLocal.get();
		
		if(cacheMap == null)
		{
			cacheMap = new HashMap<String, Object>();
			threadLocal.set(cacheMap);
		}
		
		T value = (T) cacheMap.get(key);
		
		if(value != null)
		{
			return value;
		}
		
		value = supplier.apply(key);
		cacheMap.put(key, value);
		return value;
	}
	
	public void clear()
	{
		threadLocal.remove();
	}
}
