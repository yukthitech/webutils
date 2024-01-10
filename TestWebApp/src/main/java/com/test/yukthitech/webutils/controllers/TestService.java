package com.test.yukthitech.webutils.controllers;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.test.yukthitech.webutils.models.TestBean;
import com.yukthitech.webutils.IWebUtilsInternalConstants;
import com.yukthitech.webutils.cache.WebutilsCacheEvict;
import com.yukthitech.webutils.cache.WebutilsCacheable;

@Service
public class TestService
{
	private static Logger logger = LogManager.getLogger(TestService.class);
	
	private AtomicInteger nextId = new AtomicInteger(1);
	private int count = 0;
	
	@WebutilsCacheable(groups = "#p0")
	@WebutilsCacheEvict(groups = IWebUtilsInternalConstants.CACHE_GROUP_GROUPED) 
	public TestBean getTestBean(int id)
	{
		logger.debug("Getting test bean with id: " + id);
		
		count++;
		return new TestBean("" + nextId.getAndIncrement(), id, "", "");
	}
	
	@WebutilsCacheEvict(groups = {"#p0", IWebUtilsInternalConstants.CACHE_GROUP_GROUPED}) 
	public void deleteBean(int id)
	{
		logger.debug("Deleting test bean with id: " + id);
		
		count--;
	}
	
	@WebutilsCacheEvict(allEntries = true)
	public void reset()
	{
		logger.debug("Reseting all entries");
		count = 0;
		nextId.set(1);
	}
	
	@WebutilsCacheable(groups = IWebUtilsInternalConstants.CACHE_GROUP_GROUPED)
	public int count()
	{
		logger.debug("Fetching count..");
		
		return count;
	}
}
