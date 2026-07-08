package com.webutils.services.search;

import com.webutils.common.Optional;
import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.repository.annotations.Condition;

@Optional
public interface ISearchSettingsRepository extends ICrudRepository<SearchSettingsEntity>
{
	boolean updateForUser(SearchSettingsEntity entity, @Condition("user.id") long userId);

	SearchSettingsEntity fetchByName(@Condition("user.id") long userId, @Condition("searchQueryName") String searchQueryName);

	boolean deleteByName(@Condition("user.id") long userId, @Condition("searchQueryName") String searchQueryName);

	void deleteAll();
}
