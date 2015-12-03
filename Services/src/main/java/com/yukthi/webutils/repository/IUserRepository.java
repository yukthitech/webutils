package com.yukthi.webutils.repository;

import com.yukthi.persistence.ICrudRepository;
import com.yukthi.persistence.repository.annotations.Condition;
import com.yukthi.persistence.repository.annotations.CountFunction;
import com.yukthi.persistence.repository.annotations.Field;
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
	@CountFunction
	public int checkForUser(@Condition("userName") String userName, @Condition("ownerType") String ownerType, @Condition("ownerId") long ownerId);
	
	/**
	 * Fetches encrypted password for specified user details
	 * @param userName User name
	 * @param ownerType Owner type under which user name should be present
	 * @param ownerId Owner id under which user name should be present
	 * @return Encrypted password
	 */
	@Field("password")
	public String fetchPassword(@Condition("userName") String userName, @Condition("ownerType") String ownerType, @Condition("ownerId") long ownerId);
	
	/**
	 * Fetches user with specified details
	 * @param userName User name
	 * @param ownerType Owner type under which user name should be present
	 * @param ownerId Owner id under which user name should be present
	 * @return Matching user details
	 */
	public UserEntity fetchUser(@Condition("userName") String userName, @Condition("ownerType") String ownerType, @Condition("ownerId") long ownerId);
	
	/**
	 * Used to mark an user as deleted
	 * @param userId User to be deleted
	 * @param deleted Deleted flag
	 * @param userName User name this should become null
	 */
	@UpdateFunction
	public boolean markAsDeleted(@Condition("id") long userId, @Field("deleted") boolean deleted, @Field("userName") String userName);
}
