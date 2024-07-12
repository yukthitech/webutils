package com.yukthitech.webutils.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.yukthitech.webutils.UserRoleKey;
import com.yukthitech.webutils.services.BaseCrudService;

/**
 * Service to different types of roles.
 * @author akiran
 */
@Service
public class UserRoleService extends BaseCrudService<UserRoleEntity, IUserRoleRepository>
{
	/**
	 * Instantiates a new user role service.
	 */
	public UserRoleService()
	{
		//super(UserRoleEntity.class, IUserRoleRepository.class);
	}
	
	/**
	 * Fetches the roles of the user.
	 * @param userId User for which roles needs to be fetched
	 * @return Roles assigned to user
	 */
	public List<UserRoleEntity> getUserRoles(long userId)
	{
		return super.repository.findRoles(userId, securityService.getUserSpaceIdentity());
	}
	
	/**
	 * Fetches user roles map.
	 * @param userId User for whom roles needs to be fetched
	 * @return Role map
	 */
	public Map<UserRoleKey, UserRoleEntity> getUserRoleMap(long userId)
	{
		Map<UserRoleKey, UserRoleEntity> roleMap = new HashMap<>();
		List<UserRoleEntity> rolesLst = repository.findRoles(userId, securityService.getUserSpaceIdentity());
		
		if(rolesLst == null)
		{
			return roleMap;
		}
		
		for(UserRoleEntity entity : rolesLst)
		{
			roleMap.put(new UserRoleKey(entity.getOwnerType(), entity.getOwnerId(), entity.getRole()), entity);
		}
		
		return roleMap;
	}

	@Override
	protected String getUserSpace(UserRoleEntity entity, Object model)
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
