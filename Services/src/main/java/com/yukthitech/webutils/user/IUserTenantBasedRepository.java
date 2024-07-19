package com.yukthitech.webutils.user;

import com.yukthitech.persistence.repository.annotations.AggregateFunction;
import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.persistence.repository.annotations.DefaultCondition;
import com.yukthitech.persistence.repository.annotations.Field;
import com.yukthitech.persistence.repository.annotations.MethodConditions;
import com.yukthitech.persistence.repository.annotations.SearchResult;
import com.yukthitech.persistence.repository.annotations.UpdateFunction;
import com.yukthitech.webutils.common.annotations.Conditional;

/**
 * Repository for user authentication details.
 * 
 * @author akiran
 */
@Conditional("(env['webutils.tenantSpaceBased']!'false') == 'true' || (env['webutils.user.tenantSpaceBased']!'false') == 'true'")
public interface IUserTenantBasedRepository extends IUserRepository
{
	/**
	 * Checks if a user entry is present with specified details.
	 * 
	 * @param userName
	 *            User name
	 * @param userSpace
	 *            User space
	 * @return 0 if user is not present otherwise 1
	 */
	@MethodConditions(conditions = { @DefaultCondition(field = "deleted", value = "false") })
	@AggregateFunction
	public int checkForUserBySpace(@Condition("userName") String userName, @Condition("spaceIdentity") String userSpace);

	/**
	 * Fetches encrypted password for specified user details.
	 * 
	 * @param userName
	 *            User name
	 * @param userSpace
	 *            User space
	 * @return Encrypted password
	 */
	@MethodConditions(conditions = { @DefaultCondition(field = "deleted", value = "false") })
	@SearchResult
	public UserPasswords fetchPasswordBySpace(@Condition("userName") String userName, 
			@Condition("spaceIdentity") String userSpace);

	/**
	 * Fetches user with specified details.
	 * 
	 * @param userName
	 *            User name
	 * @param userSpace
	 *            User space
	 * @return Matching user details
	 */
	@MethodConditions(conditions = { @DefaultCondition(field = "deleted", value = "false") })
	public UserEntity fetchUserBySpace(@Condition("userName") String userName, @Condition("spaceIdentity") String userSpace);

	/**
	 * Used to mark an user as deleted.
	 * 
	 * @param userId
	 *            User to be deleted
	 * @param deleted
	 *            Deleted flag
	 * @param spaceIdentity
	 *            Space to which operation should be restricted.
	 * @param userName
	 *            User name this should become null
	 * @return success/failure
	 */
	@UpdateFunction
	public boolean markDeletedBySpace(@Condition("id") long userId, @Field("deleted") boolean deleted, 
			@Field("userName") String userName, @Field("spaceIdentity") String spaceIdentity);
}
