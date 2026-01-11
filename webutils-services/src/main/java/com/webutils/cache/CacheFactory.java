package com.webutils.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class CacheFactory
{
	private Map<String, ICache<?,?>> cacheMap = new ConcurrentHashMap<String, ICache<?,?>>();
	
	@SuppressWarnings({ "unchecked", "unused" })
	public <K,V> ICache<K,V> getCache(String name, CacheConfig<K,V> config)
	{
		return (ICache<K,V>) cacheMap.computeIfAbsent(name, key -> new LocalCache<>(config));
	}
}
