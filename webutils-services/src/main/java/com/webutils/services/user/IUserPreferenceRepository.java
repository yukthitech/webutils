package com.webutils.services.user;

import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.persistence.repository.annotations.Field;

/**
 * Repository interface for User preference entity operations
 * 
 * Provides CRUD operations and custom queries for user preference management
 */
public interface IUserPreferenceRepository extends ICrudRepository<UserPreferenceEntity> 
{
    @Field(value = "value")
    UserPreferenceEntity fetchByUserIdAndKey(@Condition("user.id") Long userId, @Condition("key") String key);
}
