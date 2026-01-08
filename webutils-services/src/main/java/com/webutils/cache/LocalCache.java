package com.webutils.cache;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

public class LocalCache implements ICache
{
	private Cache<String, Object> cache;
	
	public LocalCache()
	{
		this.cache = Caffeine.newBuilder()
	            .maximumSize(Integer.MAX_VALUE)
	            .build();
	}
	
	public LocalCache(long expiryTime, TimeUnit expiryTimeUnit)
	{
		this.cache = Caffeine.newBuilder()
            .expireAfterAccess(expiryTime, expiryTimeUnit)
            .maximumSize(Integer.MAX_VALUE)
            .build();
	}
	
	@Override
	public void set(String key, Object value)
	{
		cache.put(key, value);
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	@Override
	public <T> T computeIfAbsent(String key, Supplier<T> valueSupplier)
	{
		return (T) cache.get(key, k -> valueSupplier.get());
	}
	
	@Override
	public Object get(String key)
	{
		return cache.getIfPresent(key);
	}
	
	@Override
	public void remove(String key)
	{
		cache.invalidate(key);
	}
}
