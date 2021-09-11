package com.yukthitech.webutils.repository.search;

import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.webutils.common.annotations.Optional;
import com.yukthitech.webutils.repository.IWebutilsRepository;

/**
 * Repository for user search settings.
 * @author akiran
 */
@Optional
public interface ISearchSettingsRespository extends IWebutilsRepository<SearchSettingsEntity>
{
	/**
	 * Updates the specified search settings for specified settings.
	 * @param entity Entity to update
	 * @param userId User for which settings needs to be updated.
	 * @return Success/failure
	 */
	public boolean updateForUser(SearchSettingsEntity entity, @Condition("user.id") long userId);
	
	/**
	 * Fetches settings based on specified user id and search query name.
	 * @param userId User id
	 * @param searchQueryName Search query name.
	 * @return Matching search settings for the user
	 */
	public SearchSettingsEntity fetchByName(@Condition("user.id") long userId, @Condition("searchQueryName") String searchQueryName);
	
	
	/**
	 * Deletes the search query settings of specified name.
	 * @param userId User id
	 * @param searchQueryName Search query name
	 * @return Success/failure
	 */
	public boolean deleteByName(@Condition("user.id") long userId, @Condition("searchQueryName") String searchQueryName);
	
	/**
	 * Deletes all settings.
	 */
	public void deleteAll();
}
