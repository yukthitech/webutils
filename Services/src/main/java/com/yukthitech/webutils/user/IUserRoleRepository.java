package com.yukthitech.webutils.user;

import java.util.List;

import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.webutils.common.annotations.Optional;
import com.yukthitech.webutils.repository.IWebutilsRepository;

/**
 * Repository for user authentication details.
 * @author akiran
 */
@Optional
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
