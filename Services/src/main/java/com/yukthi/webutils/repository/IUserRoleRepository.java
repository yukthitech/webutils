package com.yukthi.webutils.repository;

import java.util.List;

import com.yukthi.persistence.repository.annotations.Condition;

/**
 * Repository for user authentication details.
 * @author akiran
 */
public interface IUserRoleRepository extends IWebutilsRepository<UserRoleEntity>
{
	/**
	 * Finds roles for specified user.
	 * @param userId User for which roles needs to be fetched
	 * @param spaceIdentity Space to which fetch should be restricted.
	 * @return Matching roles
	 */
	public List<UserRoleEntity> findRoles(@Condition("user.id") long userId, @Condition("spaceIdentity") String spaceIdentity);
}
