package com.yukthitech.webutils.alerts.agent.system;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import com.yukthitech.webutils.IWebUtilsInternalConstants;
import com.yukthitech.webutils.cache.WebutilsCacheEvict;
import com.yukthitech.webutils.cache.WebutilsCacheable;
import com.yukthitech.webutils.services.BaseCrudService;
import com.yukthitech.webutils.services.IWebutilsRepositoryProxy;
import com.yukthitech.webutils.services.task.ScheduledTask;

/**
 * Service for app alert context.
 * @author akiran
 */
@CacheConfig(cacheNames = "SystemAlertContextAttr[100]")
@Service
public class SystemAlertContextAttrService extends BaseCrudService<SystemAlertContextEntity, ISystemAlertContextAttrRepository>
{
	private static Logger logger = LogManager.getLogger(SystemAlertContextAttrService.class);
	
	/**
	 * Number of days per week.
	 */
	private static final int DAYS_PER_WEEK = 7;
	
	/**
	 * Sets attribute with specified name and value.
	 * @param name name of attribute
	 * @param value value of attribute
	 */
	@WebutilsCacheEvict(groups = "#p0")
	public void setAttribute(String name, Object value)
	{
		if(super.repository.updateValue(name, value, new Date()))
		{
			return;
		}
		
		//if update is not successful, which indicates attribute does not exist
		SystemAlertContextEntity entity = new SystemAlertContextEntity();
		entity.setName(name);
		entity.setValue(value);
		
		super.save(entity, null);
	}
	
	/**
	 * Fetches the attribute value with specified name.
	 * @param name name of attr to fetch
	 * @return matching value
	 */
	@WebutilsCacheable(groups = {"#p0", IWebUtilsInternalConstants.CACHE_GROUP_GROUPED})
	public Object getAttribute(String name)
	{
		Object value = super.repository.fetchValue(name);
		
		if(value == null)
		{
			return null;
		}
		
		return value;
	}
	
	/**
	 * Removes specified attribute.
	 * @param name name of attribute to remove.
	 */
	@WebutilsCacheEvict(groups = "#p0")
	public void removeAttribute(String name)
	{
		super.repository.deleteAttribute(name);
	}
	
	/**
	 * Scheduled task method which would delete old unused attributes. Attributes which 
	 * are not update for more than week time would be deleted.
	 */
	@WebutilsCacheEvict(groups = IWebUtilsInternalConstants.CACHE_GROUP_GROUPED)
	@ScheduledTask(time = "01:00 am")
	public void deleteUnusedAttributes()
	{
		if(super.repository instanceof IWebutilsRepositoryProxy)
		{
			IWebutilsRepositoryProxy proxy = (IWebutilsRepositoryProxy) super.repository;
			
			if(!proxy.isRepositoryEnabled())
			{
				return;
			}
		}
		
		Date oneWeekBack = DateUtils.addDays(new Date(), - DAYS_PER_WEEK);
		int count = super.repository.deleteOldAttributes(oneWeekBack);
		
		logger.debug("Deleted {} old app alert context attributes", count);
	}
}
