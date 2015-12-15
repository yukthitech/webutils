package com.yukthi.webutils.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.yukthi.webutils.UserRoleKey;
import com.yukthi.webutils.repository.IUserRoleRepository;
import com.yukthi.webutils.repository.UserRoleEntity;

/**
 * Service to different types of roles
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
		super(UserRoleEntity.class, IUserRoleRepository.class);
	}
	
	/**
	 * Fetches the roles of the user
	 * @param userId User for which roles needs to be fetched
	 * @return Roles assigned to user
	 */
	public List<UserRoleEntity> getUserRoles(long userId)
	{
		return super.repository.findRoles(userId);
	}
	
	/**
	 * Fetches user roles map
	 * @param userId User for whom roles needs to be fetched
	 * @return Role map
	 */
	public Map<UserRoleKey, UserRoleEntity> getUserRoleMap(long userId)
	{
		Map<UserRoleKey, UserRoleEntity> roleMap = new HashMap<>();
		List<UserRoleEntity> rolesLst = repository.findRoles(userId);
		
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
}
