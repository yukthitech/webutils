package com.yukthitech.webutils.user;

import com.yukthitech.persistence.repository.annotations.AggregateFunction;
import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.persistence.repository.annotations.DefaultCondition;
import com.yukthitech.persistence.repository.annotations.Field;
import com.yukthitech.persistence.repository.annotations.MethodConditions;
import com.yukthitech.persistence.repository.annotations.SearchResult;
import com.yukthitech.persistence.repository.annotations.UpdateFunction;
import com.yukthitech.webutils.common.annotations.Conditional;
import com.yukthitech.webutils.repository.IWebutilsRepository;

/**
 * Repository for user authentication details.
 * 
 * @author akiran
 */
@Conditional("(env['webutils.tenantSpaceBased']!'false') != 'true' && (env['webutils.user.tenantSpaceBased']!'false') != 'true'")
public interface IUserRepository extends IWebutilsRepository<UserEntity>
{
	/**
	 * Checks if a user entry is present with specified details.
	 * 
	 * @param userName
	 *            User name
	 * @return 0 if user is not present otherwise 1
	 */
	@MethodConditions(conditions = { @DefaultCondition(field = "deleted", value = "false") })
	@AggregateFunction
	public int checkForUser(@Condition("userName") String userName);

	/**
	 * Fetches encrypted password for specified user details.
	 * 
	 * @param userName
	 *            User name
	 * @return Encrypted password
	 */
	@MethodConditions(conditions = { @DefaultCondition(field = "deleted", value = "false") })
	@SearchResult
	public UserPasswords fetchPassword(@Condition("userName") String userName);

	@MethodConditions(conditions = { @DefaultCondition(field = "deleted", value = "false") })
	@Field("password")
	public String fetchPasswordById(@Condition("id") long id);

	/**
	 * Fetches user with specified details.
	 * 
	 * @param userName
	 *            User name
	 * @return Matching user details
	 */
	@MethodConditions(conditions = { @DefaultCondition(field = "deleted", value = "false") })
	public UserEntity fetchUser(@Condition("userName") String userName);

	/**
	 * Used to mark an user as deleted.
	 * 
	 * @param userId
	 *            User to be deleted
	 * @param deleted
	 *            Deleted flag
	 * @param userName
	 *            User name this should become null
	 * @return success/failure
	 */
	@UpdateFunction
	public boolean markAsDeleted(@Condition("id") long userId, 
			@Field("deleted") boolean deleted, @Field("userName") String userName);

	/**
	 * Fetches user based on base entity details.
	 * 
	 * @param baseEntityType
	 *            Base entity type for which this user is created
	 * @param baseEntityId
	 *            Base entity id for which this user is created
	 * @return Matching user entity
	 */
	@MethodConditions(conditions = { @DefaultCondition(field = "deleted", value = "false") })
	public UserEntity fetchUserByBaseEntity(@Condition("baseEntityType") String baseEntityType, @Condition("baseEntityId") long baseEntityId);

	/**
	 * Marks user as deleted based on specified based entity type and id.
	 * 
	 * @param baseEntityType
	 *            Base entity type
	 * @param baseEntityId
	 *            Base entity id
	 * @param deleted
	 *            Deleted flag
	 * @return Success/failure
	 */
	@UpdateFunction
	public boolean markDeletedByBaseEntity(@Condition("baseEntityType") String baseEntityType, 
			@Condition("baseEntityId") long baseEntityId, @Field("deleted") boolean deleted);

	/**
	 * Updates password of user under specified ownership and with specified
	 * user name.
	 * 
	 * @param userName
	 *            User name
	 * @param password
	 *            Password
	 * @return Success/failure
	 */
	public boolean updatePassword(@Condition("id") long userId, 
			@Field("password") String password, 
			@Field(value = "resetPassword") String resetPassword);

	public boolean updateResetPassword(@Condition("id") long userId, 
			@Field(value = "resetPassword") String resetPassword);
}
