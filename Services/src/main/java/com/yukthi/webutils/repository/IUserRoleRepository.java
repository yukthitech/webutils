package com.yukthi.webutils.repository;

import java.util.List;

import com.yukthi.persistence.ICrudRepository;
import com.yukthi.persistence.repository.annotations.Condition;

/**
 * Repository for user authentication details
 * @author akiran
 */
public interface IUserRoleRepository extends ICrudRepository<UserRoleEntity>
{
	/**
	 * Finds roles for specified user
	 * @param userId User for which roles needs to be fetched
	 * @return Matching roles
	 */
	public List<UserRoleEntity> findRoles(@Condition("user.id") long userId);
}
