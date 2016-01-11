package com.yukthi.webutils.services;

import org.springframework.stereotype.Service;

import com.yukthi.webutils.repository.IUserRepository;
import com.yukthi.webutils.repository.UserEntity;

/**
 * Service to access authentication details of different types of users
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
		super(UserEntity.class, IUserRepository.class);
	}
	
	/**
	 * Checks if a user is already registered with specified details
	 * @param userName Name of the user
	 * @param ownerType Owner type
	 * @param ownerId Owner id
	 * @return True if the user is already present
	 */
	public boolean checkForUser(String userName, String ownerType, long ownerId)
	{
		return (repository.checkForUser(userName, ownerType, ownerId) > 0);
	}
	
	/**
	 * Fetches encrypted password for specified user details
	 * @param userName User name
	 * @param ownerType Owner type under which user is registered
	 * @param ownerId Owner id under which user is registered
	 * @return Encrypted password
	 */
	public String getPassword(String userName, String ownerType, long ownerId)
	{
		return super.repository.fetchPassword(userName, ownerType, ownerId);
	}
	
	/**
	 * Fetches user with specified details
	 * @param userName User name
	 * @param ownerType Owner type under which user is registered
	 * @param ownerId Owner id under which user is registered
	 * @return Matching user details
	 */
	public UserEntity getUser(String userName, String ownerType, long ownerId)
	{
		return super.repository.fetchUser(userName, ownerType, ownerId);
	}
	
	/**
	 * Fetches user based on base entity details
	 * @param baseEntityType Base entity type for which this user is created
	 * @param baseEntityId Base entity id for which this user is created
	 * @return Matching user entity
	 */
	public UserEntity fetchUser(String baseEntityType, long baseEntityId)
	{
		return super.repository.fetchUserByBaseEntity(baseEntityType, baseEntityId);
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.services.BaseCrudService#deleteById(long)
	 */
	@Override
	public boolean deleteById(long id)
	{
		return super.repository.markAsDeleted(id, true, null);
	}
}
