package com.webutils.services.user;

import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.persistence.repository.annotations.DefaultCondition;
import com.yukthitech.persistence.repository.annotations.Field;
import com.yukthitech.persistence.repository.annotations.Operator;
import com.yukthitech.persistence.repository.annotations.UpdateFunction;

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
    		@Condition(value = "customSpace") String customSpace);
    
    /**
     * Update email address for a user.
     * 
     * @param email Email address to set
     * @param id User ID
     * @return true if update was successful
     */
    @UpdateFunction
    boolean updateEmail(@Field("email") String email, @Condition("id") Long id);
}
