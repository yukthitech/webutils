package com.webutils.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

@Service
public class CacheFactory
{
	private Map<String, ICache> cacheMap = new ConcurrentHashMap<String, ICache>();
	
	@SuppressWarnings("unused")
	public ICache getCache(String name, long expiryTime, TimeUnit expiryTimeUnit)
	{
		return cacheMap.computeIfAbsent(name, key -> new LocalCache(expiryTime, expiryTimeUnit));
	}

	@SuppressWarnings("unused")
	public ICache getCache(String name)
	{
		return cacheMap.computeIfAbsent(name, key -> new LocalCache());
	}
}
