package com.yukthi.webutils.repository;

import com.yukthi.persistence.repository.annotations.Condition;
import com.yukthi.persistence.repository.annotations.CountFunction;
import com.yukthi.persistence.repository.annotations.DefaultCondition;
import com.yukthi.persistence.repository.annotations.Field;
import com.yukthi.persistence.repository.annotations.MethodConditions;
import com.yukthi.persistence.repository.annotations.UpdateFunction;

/**
 * Repository for user authentication details.
 * @author akiran
 */
public interface IUserRepository extends IWebutilsRepository<UserEntity>
{
	/**
	 * Checks if a user entry is present with specified details.
	 * @param userName User name
	 * @param userSpace User space
	 * @return 0 if user is not present otherwise 1
	 */
	@MethodConditions(conditions = {
		@DefaultCondition(field = "deleted", value = "false")
		})
	@CountFunction
	public int checkForUser(@Condition("userName") String userName, @Condition("spaceIdentity") String userSpace);

	/**
	 * Fetches encrypted password for specified user details.
	 * @param userName User name
	 * @param userSpace User space
	 * @return Encrypted password
	 */
	@MethodConditions(conditions = {
			@DefaultCondition(field = "deleted", value = "false")
		})
	@Field("password")
	public String fetchPassword(@Condition("userName") String userName, @Condition("spaceIdentity") String userSpace);
	
	/**
	 * Fetches user with specified details.
	 * @param userName User name
	 * @param userSpace User space
	 * @return Matching user details
	 */
	@MethodConditions(conditions = {
			@DefaultCondition(field = "deleted", value = "false")
		})
	public UserEntity fetchUser(@Condition("userName") String userName, @Condition("spaceIdentity") String userSpace);
	
	/**
	 * Used to mark an user as deleted.
	 * @param userId User to be deleted
	 * @param deleted Deleted flag
	 * @param spaceIdentity Space to which operation should be restricted.
	 * @param userName User name this should become null
	 * @return success/failure
	 */
	@UpdateFunction
	public boolean markAsDeleted(@Condition("id") long userId, @Field("deleted") boolean deleted, @Field("userName") String userName, @Field("spaceIdentity") String spaceIdentity);
	
	/**
	 * Fetches user based on base entity details.
	 * @param baseEntityType Base entity type for which this user is created
	 * @param baseEntityId Base entity id for which this user is created
	 * @return Matching user entity
	 */
	@MethodConditions(conditions = {
			@DefaultCondition(field = "deleted", value = "false")
		})
	public UserEntity fetchUserByBaseEntity(@Condition("baseEntityType") String baseEntityType, @Condition("baseEntityId") long baseEntityId);

	/**
	 * Marks user as deleted based on specified based entity type and id. 
	 * @param baseEntityType Base entity type
	 * @param baseEntityId Base entity id
	 * @param deleted Deleted flag
	 * @return Success/failure
	 */
	@UpdateFunction
	public boolean markDeletedByBaseEntity(@Condition("baseEntityType") String baseEntityType, @Condition("baseEntityId") long baseEntityId, @Field("deleted") boolean deleted);
	
	/**
	 * Updates password of user under specified ownership and with specified user name.
	 * @param userSpace User space
	 * @param userName User name
	 * @param password Password
	 * @return Success/failure
	 */
	public boolean updatePassword(@Condition("spaceIdentity") String userSpace, @Condition("userName") String userName, @Field("password") String password);
}
