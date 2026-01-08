package com.webutils.services.user;

import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.persistence.repository.annotations.DefaultCondition;
import com.yukthitech.persistence.repository.annotations.Operator;

/**
 * Repository interface for User entity operations
 * 
 * Provides CRUD operations and custom queries for user management
 */
public interface IUserRepository extends ICrudRepository<UserEntity> 
{
    @DefaultCondition(field = "active", value = "true", op = Operator.EQ)
    UserEntity fetchUserByEmail(
    		@Condition(value = "email") String email,
    		@Condition(value = "role") String role);
}
