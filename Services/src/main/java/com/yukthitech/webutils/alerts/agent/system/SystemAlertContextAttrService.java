package com.yukthitech.webutils.alerts.agent.system;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.yukthitech.webutils.services.BaseCrudService;
import com.yukthitech.webutils.services.task.ScheduledTask;

/**
 * Service for app alert context.
 * @author akiran
 */
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
	public Object getAttribute(String name)
	{
		Object value = super.repository.fetchValue(name);
		
		if(value == null)
		{
			return null;
		}
		
		super.repository.updateAccessTime(name, new Date());
		return value;
	}
	
	/**
	 * Scheduled task method which would delete old unused attributes. Attributes which 
	 * are not update for more than week time would be deleted.
	 */
	@ScheduledTask(time = "01:00 am")
	public void deleteUnusedAttributes()
	{
		Date oneWeekBack = DateUtils.addDays(new Date(), - DAYS_PER_WEEK);
		int count = super.repository.deleteOldAttributes(oneWeekBack);
		
		logger.debug("Deleted {} old app alert context attributes", count);
	}
}
