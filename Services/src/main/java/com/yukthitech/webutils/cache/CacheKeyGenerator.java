package com.yukthitech.webutils.cache;

import java.lang.reflect.Method;

/**
 * Used to generate cache key in webutils standard.
 * @author akiran
 */
public class CacheKeyGenerator extends AbstractCacheKeyGenerator
{
	
	/* (non-Javadoc)
	 * @see org.springframework.cache.interceptor.KeyGenerator#generate(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public Object generate(Object target, Method method, Object... params)
	{
		WebutilsCacheable webutilsCacheable = method.getAnnotation(WebutilsCacheable.class);
		
		if(webutilsCacheable != null)
		{
			return constructKey(
					webutilsCacheable.cachekey(), 
					webutilsCacheable.groups(), 
					webutilsCacheable.excludeMethodName(), 
					webutilsCacheable.excludeParams(), 
					target, method, params);
		}
		
		return constructKey(null, null, false, false, target, method, params);
	}
}
