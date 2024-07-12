package com.yukthitech.webutils.user;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.yukthitech.webutils.services.BaseCrudService;

/**
 * Service to access authentication details of different types of users.
 * 
 * @author akiran
 */
@Service
public class UserService extends BaseCrudService<UserEntity, IUserRepository>
{
	/**
	 * Instantiates a new user service.
	 */
	public UserService()
	{
	}

	/**
	 * Checks if a user is already registered with specified details.
	 * 
	 * @param userName
	 *            Name of the user
	 * @param userSpace
	 *            User Space
	 * @return True if the user is already present
	 */
	public boolean checkForUser(String userName, String userSpace)
	{
		int count = 0;
		
		if(repository instanceof IUserTenantBasedRepository)
		{
			count = ((IUserTenantBasedRepository) repository).checkForUser(userName, userSpace);
		}
		else
		{
			count = repository.checkForUser(userName);
		}
		
		return (count > 0);
	}

	/**
	 * Fetches encrypted password for specified user details.
	 * 
	 * @param userName
	 *            User name
	 * @param userSpace
	 *            User Space
	 * @return Encrypted password
	 */
	public UserPasswords getPassword(String userName, String userSpace)
	{
		if(repository instanceof IUserTenantBasedRepository)
		{
			return ((IUserTenantBasedRepository) repository).fetchPassword(userName, userSpace);
		}
		
		return repository.fetchPassword(userName);
	}
	
	public String getPassword(long id)
	{
		return repository.fetchPasswordById(id);
	}
	

	/**
	 * Fetches user with specified details.
	 * 
	 * @param userName
	 *            User name
	 * @param userSpace
	 *            User Space
	 * @return Matching user details
	 */
	public UserEntity getUser(String userName, String userSpace)
	{
		if(repository instanceof IUserTenantBasedRepository)
		{
			return ((IUserTenantBasedRepository) repository).fetchUser(userName, userSpace);
		}
		
		return repository.fetchUser(userName);
	}

	/**
	 * Fetches user based on base entity details.
	 * 
	 * @param baseEntityType
	 *            Base entity type for which this user is created
	 * @param baseEntityId
	 *            Base entity id for which this user is created
	 * @return Matching user entity
	 */
	public UserEntity fetchUserByBaseEntity(String baseEntityType, long baseEntityId)
	{
		return super.repository.fetchUserByBaseEntity(baseEntityType, baseEntityId);
	}

	/**
	 * Marks user as deleted based on specified base entity type and id.
	 * 
	 * @param baseEntityType
	 *            Base entity type for which this user is created
	 * @param baseEntityId
	 *            Base entity id for which this user is created
	 * @return true if deletion was successful
	 */
	public boolean deleteByBaseEntity(String baseEntityType, long baseEntityId)
	{
		return super.repository.markDeletedByBaseEntity(baseEntityType, baseEntityId, true);
	}

	/**
	 * Updates password of user under specified ownership and with specified
	 * user name.
	 * 
	 * @param userId
	 *            User id
	 * @param password Password of target user.
	 * @return True if successfully updated
	 */
	public boolean updatePassword(long userId, String password)
	{
		// Whenever password is getting updated, set the reset-password to null
		return repository.updatePassword(userId, password, null);
	}

	public boolean updateResetPassword(long userId, String resetPassword)
	{
		return repository.updateResetPassword(userId, resetPassword);
	}

	@Override
	public boolean deleteById(long id)
	{
		if(repository instanceof IUserTenantBasedRepository)
		{
			return ((IUserTenantBasedRepository) repository).markAsDeleted(id, true, null, securityService.getUserSpaceIdentity());
		}
		
		return repository.markAsDeleted(id, true, null);
	}

	@Override
	protected String getUserSpace(UserEntity entity, Object model)
	{
		// As the user space needs to be handled by apps this method is
		// overridden
		// this method will check if entity has user space, if present the same
		// will be used.
		// if not, default user space will be used.

		if(StringUtils.isNotBlank(entity.getSpaceIdentity()))
		{
			return entity.getSpaceIdentity();
		}

		return super.getUserSpace(entity, model);
	}
}
