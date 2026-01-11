package com.webutils.cache;

import java.util.function.Supplier;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

public class LocalCache<K,V> implements ICache<K,V>
{
	private Cache<K, V> cache;
	
	public LocalCache(CacheConfig<K,V> config)
	{
		Caffeine<Object, Object> builder = Caffeine.newBuilder();

		if(config.getExpiryTime() > 0)
		{
			builder.expireAfterAccess(config.getExpiryTime(), config.getExpiryTimeUnit());
		}

		if(config.getMaxSize() > 0)
		{
			builder.maximumSize(config.getMaxSize());
		}

		this.cache = builder.build();
	}
	
	@Override
	public void set(K key, V value)
	{
		cache.put(key, value);
	}
	
	@SuppressWarnings("unused")
	@Override
	public V computeIfAbsent(K key, Supplier<V> valueSupplier)
	{
		return cache.get(key, k -> valueSupplier.get());
	}
	
	@Override
	public V get(K key)
	{
		return cache.getIfPresent(key);
	}
	
	@Override
	public void remove(K key)
	{
		cache.invalidate(key);
	}
}
