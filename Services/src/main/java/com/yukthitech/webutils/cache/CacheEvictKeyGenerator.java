package com.yukthitech.webutils.cache;

import java.lang.reflect.Method;

/**
 * Used to generate cache key in webutils standard.
 * @author akiran
 */
public class CacheEvictKeyGenerator extends AbstractCacheKeyGenerator
{
	
	/* (non-Javadoc)
	 * @see org.springframework.cache.interceptor.KeyGenerator#generate(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public Object generate(Object target, Method method, Object... params)
	{
		WebutilsCacheEvict webutilsCacheEvict = method.getAnnotation(WebutilsCacheEvict.class);

		if(webutilsCacheEvict != null)
		{
			CacheKey key = constructKey(
					webutilsCacheEvict.key(), 
					webutilsCacheEvict.groups(), 
					webutilsCacheEvict.excludeMethodName(), 
					webutilsCacheEvict.excludeParams(), 
					target, method, params);
			
			if(webutilsCacheEvict.allEntries())
			{
				key.setRemoveAllEntries(true);
			}
			
			return key;
		}
		
		return constructKey(null, null, false, false, target, method, params);
	}
}
