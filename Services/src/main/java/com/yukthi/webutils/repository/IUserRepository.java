package com.yukthi.webutils.repository;

import com.yukthi.persistence.ICrudRepository;
import com.yukthi.persistence.repository.annotations.Condition;
import com.yukthi.persistence.repository.annotations.CountFunction;
import com.yukthi.persistence.repository.annotations.DefaultCondition;
import com.yukthi.persistence.repository.annotations.Field;
import com.yukthi.persistence.repository.annotations.MethodConditions;
import com.yukthi.persistence.repository.annotations.UpdateFunction;

/**
 * Repository for user authentication details
 * @author akiran
 */
public interface IUserRepository extends ICrudRepository<UserEntity>
{
	/**
	 * Checks if a user entry is present with specified details
	 * @param userName User name
	 * @param ownerType Owner type under which user name should be present
	 * @param ownerId Owner id under which user name should be present
	 * @return 0 if user is not present otherwise 1
	 */
	@MethodConditions(conditions = {
		@DefaultCondition(field = "deleted", value = "false")
	})
	@CountFunction
	public int checkForUser(@Condition("userName") String userName, @Condition("ownerType") String ownerType, @Condition("ownerId") long ownerId);
	
	/**
	 * Fetches encrypted password for specified user details
	 * @param userName User name
	 * @param ownerType Owner type under which user name should be present
	 * @param ownerId Owner id under which user name should be present
	 * @return Encrypted password
	 */
	@MethodConditions(conditions = {
			@DefaultCondition(field = "deleted", value = "false")
		})
	@Field("password")
	public String fetchPassword(@Condition("userName") String userName, @Condition("ownerType") String ownerType, @Condition("ownerId") long ownerId);
	
	/**
	 * Fetches user with specified details
	 * @param userName User name
	 * @param ownerType Owner type under which user name should be present
	 * @param ownerId Owner id under which user name should be present
	 * @return Matching user details
	 */
	@MethodConditions(conditions = {
			@DefaultCondition(field = "deleted", value = "false")
		})
	public UserEntity fetchUser(@Condition("userName") String userName, @Condition("ownerType") String ownerType, @Condition("ownerId") long ownerId);
	
	/**
	 * Used to mark an user as deleted
	 * @param userId User to be deleted
	 * @param deleted Deleted flag
	 * @param userName User name this should become null
	 */
	@UpdateFunction
	public boolean markAsDeleted(@Condition("id") long userId, @Field("deleted") boolean deleted, @Field("userName") String userName);
	
	
	/**
	 * Fetches user based on base entity details
	 * @param baseEntityType Base entity type for which this user is created
	 * @param baseEntityId Base entity id for which this user is created
	 * @return Matching user entity
	 */
	@MethodConditions(conditions = {
			@DefaultCondition(field = "deleted", value = "false")
		})
	public UserEntity fetchUserByBaseEntity(@Condition("baseEntityType") String baseEntityType, @Condition("baseEntityId") long baseEntityId);

	/**
	 * Marks user as deleted based on specified based entity type and id 
	 * @param baseEntityType
	 * @param baseEntityId
	 * @return
	 */
	@UpdateFunction
	public boolean markDeletedByBaseEntity(@Condition("baseEntityType") String baseEntityType, @Condition("baseEntityId") long baseEntityId, @Field("deleted") boolean deleted);
}
