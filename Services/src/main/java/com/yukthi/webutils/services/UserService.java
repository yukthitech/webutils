package com.yukthi.webutils.services;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.yukthi.webutils.repository.IUserRepository;
import com.yukthi.webutils.repository.UserEntity;

/**
 * Service to access authentication details of different types of users.
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
	 * Checks if a user is already registered with specified details.
	 * @param userName Name of the user
	 * @param userSpace User Space
	 * @return True if the user is already present
	 */
	public boolean checkForUser(String userName, String userSpace)
	{
		return (repository.checkForUser(userName, userSpace) > 0);
	}
	
	/**
	 * Fetches encrypted password for specified user details.
	 * @param userName User name
	 * @param userSpace User Space
	 * @return Encrypted password
	 */
	public String getPassword(String userName, String userSpace)
	{
		return super.repository.fetchPassword(userName, userSpace);
	}
	
	/**
	 * Fetches user with specified details.
	 * @param userName User name
	 * @param userSpace User Space
	 * @return Matching user details
	 */
	public UserEntity getUser(String userName, String userSpace)
	{
		return super.repository.fetchUser(userName, userSpace);
	}
	
	/**
	 * Fetches user based on base entity details.
	 * @param baseEntityType Base entity type for which this user is created
	 * @param baseEntityId Base entity id for which this user is created
	 * @return Matching user entity
	 */
	public UserEntity fetchUserByBaseEntity(String baseEntityType, long baseEntityId)
	{
		return super.repository.fetchUserByBaseEntity(baseEntityType, baseEntityId);
	}

	/**
	 * Marks user as deleted based on specified base entity type and id.
	 * @param baseEntityType Base entity type for which this user is created
	 * @param baseEntityId Base entity id for which this user is created
	 * @return true if deletion was successful
	 */
	public boolean deleteByBaseEntity(String baseEntityType, long baseEntityId)
	{
		return super.repository.markDeletedByBaseEntity(baseEntityType, baseEntityId, true);
	}
	
	/**
	 * Updates password of user under specified ownership and with specified user name.
	 * @param userSpace User Space
	 * @param userName
	 * @param password
	 * @return
	 */
	public boolean updatePassword(String userSpace, String userName, String password)
	{
		return super.repository.updatePassword(userSpace, userName, password);
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.services.BaseCrudService#deleteById(long)
	 */
	@Override
	public boolean deleteById(long id)
	{
		return super.repository.markAsDeleted(id, true, null, securityService.getUserSpaceIdentity());
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.services.BaseCrudService#getUserSpace(com.yukthi.webutils.repository.WebutilsEntity, java.lang.Object)
	 */
	@Override
	protected String getUserSpace(UserEntity entity, Object model)
	{
		//As the user space needs to be handled by apps this method is overridden
		//	this method will check if entity has user space, if present the same will be used.
		//	if not, default user space will be used.
		
		if( StringUtils.isNotBlank(entity.getSpaceIdentity()) )
		{
			return entity.getSpaceIdentity();
		}
		
		return super.getUserSpace(entity, model);
	}
}
