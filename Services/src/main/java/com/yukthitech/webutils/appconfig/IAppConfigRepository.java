package com.yukthitech.webutils.appconfig;

import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.webutils.common.annotations.Optional;
import com.yukthitech.webutils.repository.IWebutilsRepository;

/**
 * Repository for message parsing rules.
 * @author akiran
 */
@Optional
public interface IAppConfigRepository extends IWebutilsRepository<AppConfigEntity>
{
	/**
	 * Fetches app configuration with specified name.
	 * @param name name of configuration to fetch
	 * @return matching config
	 */
	public AppConfigEntity fetchByName(@Condition("name") String name);
	
	/**
	 * Deletes the configuration with specified name.
	 * @param name name of config to delete.
	 * @return true if deleted
	 */
	public boolean deleteByName(@Condition("name") String name);
}
