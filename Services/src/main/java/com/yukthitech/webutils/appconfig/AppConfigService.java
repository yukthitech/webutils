package com.yukthitech.webutils.appconfig;

import org.springframework.stereotype.Service;

import com.yukthitech.webutils.services.BaseCrudService;

/**
 * Config service to maintain app configuration.
 * @author akiran
 */
@Service
public class AppConfigService extends BaseCrudService<AppConfigEntity, IAppConfigRepository>
{
	/**
	 * Sets the configuration with specified name and value.
	 * @param name name of config
	 * @param value value of config
	 */
	public void set(String name, Object value)
	{
		AppConfigEntity config = super.repository.fetchByName(name);
		
		if(config == null)
		{
			config = new AppConfigEntity(name, value);
			super.save(config, null);
		}
		else
		{
			super.update(config, null);
		}
	}
	
	/**
	 * Fetches config value with specified name.
	 * @param name name of config
	 * @return matching config value
	 */
	public Object get(String name)
	{
		AppConfigEntity config = super.repository.fetchByName(name);
		
		if(config == null)
		{
			return null;
		}
		
		return config.getValue();
	}
	
	/**
	 * Deletes the config with specified name.
	 * @param name name of config to delete
	 * @return true if deleted.
	 */
	public boolean delete(String name)
	{
		return super.repository.deleteByName(name);
	}
}
