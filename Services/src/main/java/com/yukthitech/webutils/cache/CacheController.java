package com.yukthitech.webutils.cache;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.yukthitech.webutils.annotations.ActionName;
import com.yukthitech.webutils.common.cache.ICacheController;
import com.yukthitech.webutils.common.client.IRequestCustomizer;
import com.yukthitech.webutils.common.models.BaseResponse;
import com.yukthitech.webutils.controllers.BaseController;

/**
 * Controller for managing cache.
 * @author akiran
 */
@RestController
@RequestMapping("/cache")
@ActionName("cache")
public class CacheController extends BaseController implements ICacheController
{
	private static Logger logger = LogManager.getLogger(CacheController.class);
	
	/**
	 * Cache manager to be managed.
	 */
	@Autowired
	private WebutilsSpringCacheManager webutilsSpringCacheManager;
	
	@ActionName("clearCache")
	@RequestMapping(value = "/clearCache", method = RequestMethod.GET)
	@ResponseBody
	@Override
	public BaseResponse clearCache()
	{
		Collection<String> cacheNames = webutilsSpringCacheManager.getCacheNames();
		
		if(cacheNames == null || cacheNames.isEmpty())
		{
			return new BaseResponse();
		}
		
		for(String name : cacheNames)
		{
			Cache cache = webutilsSpringCacheManager.getCache(name);
			
			if(cache == null)
			{
				continue;
			}
			
			cache.clear();
		}
		
		logger.debug("Cleared caches with name: {}", cacheNames);
		return new BaseResponse();
	}

	@Override
	public CacheController setRequestCustomizer(IRequestCustomizer customizer)
	{
		return null;
	}
}
