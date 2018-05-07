package com.yukthitech.webutils.cache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.commons.collections4.map.LRUMap;
import org.springframework.cache.Cache;

import com.yukthitech.utils.ConvertUtils;
import com.yukthitech.utils.exceptions.InvalidArgumentException;

/**
 * Custom spring cache implementation.
 * @author akiran
 */
public class WebutilsCache implements Cache
{
	/**
	 * Spring value wrapper implementation.
	 * @author akiran
	 */
	private static class CacheValueWrapper implements Cache.ValueWrapper
	{
		/**
		 * Value being wrapped.
		 */
		private Object value;
		
		/**
		 * Instantiates a new cache value wrapper.
		 *
		 * @param value the value
		 */
		public CacheValueWrapper(Object value)
		{
			this.value = value;
		}

		@Override
		public Object get()
		{
			return value;
		}
	}
	
	/**
	 * Name of the cache.
	 */
	private String name;
	
	/**
	 * Map used for caching.
	 */
	private Map<Object, CacheValueWrapper> cacheMap = null;
	
	/**
	 * Mapping from group to keys. Which in turn is used during eviction.
	 */
	private Map<String, Set<Object>> groupToKeys = new HashMap<>();
	
	/**
	 * Instantiates a new webutils cache.
	 *
	 * @param name the name
	 * @param cacheSize size of lru size to maintain
	 */
	public WebutilsCache(String name, int cacheSize)
	{
		if(cacheSize < 1)
		{
			throw new InvalidArgumentException("Cache size can not be less than 1. Specified size: {}", cacheSize);
		}
		
		this.name = name;
		
		cacheMap = new LRUMap<Object, CacheValueWrapper>(cacheSize)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected boolean removeLRU(LinkEntry<Object, CacheValueWrapper> entry)
			{
				cleanKeyGroups(entry.getKey());
				return true;
			}
		};
	}
	
	/**
	 * Cleans up groups linked with this key.
	 * @param key key being removed
	 */
	private void cleanKeyGroups(Object key)
	{
		if(!(key instanceof CacheKey))
		{
			return;
		}
		
		CacheKey cacheKey = (CacheKey) key;
		Set<String> groups = cacheKey.getGroups();
		
		if(groups == null || groups.isEmpty())
		{
			return;
		}
		
		Set<Object> groupKeys = null;
		
		for(String group : groups)
		{
			groupKeys = groupToKeys.get(group);
			
			if(groupKeys == null)
			{
				continue;
			}
			
			groupKeys.remove(group);
		}
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public Object getNativeCache()
	{
		return null;
	}

	@Override
	public synchronized ValueWrapper get(Object key)
	{
		return cacheMap.get(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized <T> T get(Object key, Class<T> type)
	{
		CacheValueWrapper valueWrap = cacheMap.get(key);
		
		if(valueWrap == null)
		{
			return null;
		}
		
		return (T) ConvertUtils.convert(valueWrap.value, type);
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized <T> T get(Object key, Callable<T> valueLoader)
	{
		CacheValueWrapper val = cacheMap.get(key);
		
		if(val != null)
		{
			return (T) val.value;
		}
		
		try
		{
			return valueLoader.call();
		}catch(Exception ex)
		{
			throw new ValueRetrievalException(key, valueLoader, ex);
		}
	}

	@Override
	public synchronized void put(Object key, Object value)
	{
		cacheMap.put(key, new CacheValueWrapper(value) );
		
		if(key instanceof CacheKey)
		{
			CacheKey cacheKey = (CacheKey) key;
			Set<String> groups = cacheKey.getGroups();
			
			if(groups == null || groups.isEmpty())
			{
				return;
			}
			
			for(String group : groups)
			{
				Set<Object> keySet = groupToKeys.get(group);
				
				if(keySet == null)
				{
					keySet = new HashSet<>();
					groupToKeys.put(group, keySet);
				}
				
				keySet.add(key);
			}
		}
	}

	@Override
	public synchronized ValueWrapper putIfAbsent(Object key, Object value)
	{
		CacheValueWrapper val = cacheMap.get(key);
		
		if(val != null)
		{
			return val;
		}
		
		put(key, value);
		return null;
	}

	@Override
	public synchronized void evict(Object key)
	{
		/*
		 * Check if group is specified, if specified
		 * remove all keys matching with specified group.
		 */
		if(key instanceof CacheKey)
		{
			CacheKey cacheKey = (CacheKey) key;
			Set<String> groups = cacheKey.getGroups();
			
			if(groups != null && !groups.isEmpty())
			{
				for(String group : groups)
				{
					Set<Object> keySet = groupToKeys.get(group);
					
					if(keySet == null)
					{
						continue;
					}
	
					for(Object gkey : keySet)
					{
						cacheMap.remove(gkey);
					}
					
					keySet.clear();
				}
				
				return;
			}
			
			if(cacheKey.isRemoveAllEntries())
			{
				clear();
				return;
			}
		}
		
		cacheMap.remove(key);
		cleanKeyGroups(key);
	}

	@Override
	public synchronized void clear()
	{
		cacheMap.clear();
		groupToKeys.clear();
	}
}
