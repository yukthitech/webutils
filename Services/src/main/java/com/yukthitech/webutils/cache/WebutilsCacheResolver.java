package com.yukthitech.webutils.cache;

import java.util.Arrays;
import java.util.Collection;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.SimpleCacheResolver;

/**
 * Custom spring cache resolver, which defaults the cache name to target class name.
 * @author akiran
 */
public class WebutilsCacheResolver extends SimpleCacheResolver
{
	/**
	 * To fetch missing caches.
	 */
	@Autowired
	private WebutilsSpringCacheManager cacheManager;
	
	/**
	 * Used to set cache manager.
	 */
	@PostConstruct
	private void init()
	{
		super.setCacheManager(cacheManager);
	}
	
	@Override
	public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context)
	{
		Collection<? extends Cache> res = super.resolveCaches(context);
		
		if(res != null && !res.isEmpty())
		{
			return res;
		}
		
		String cacheName = context.getTarget().getClass().getName();
		return Arrays.asList(cacheManager.getCache(cacheName));
	}
}
