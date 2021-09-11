package com.yukthitech.webutils.repository;

import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.webutils.common.annotations.Conditional;

/**
 * Defines common repository methods with limitations on space.
 * @author akiran
 * @param <E> Entity type
 */
@Conditional("false")
public interface ITenantBasedRepository<E extends WebutilsBaseEntity & ITenantSpaceBased> extends IWebutilsRepository<E>
{
	/**
	 * Fetches entity based on id and restricting to specified space identity.
	 * @param id entity id.
	 * @param spaceIdentity Space identity string to which search should be restricted.
	 * @return Matching entity.
	 */
	public E findByIdAndUserSpace(@Condition("id") Object id, @Condition("spaceIdentity") String spaceIdentity);
	
	/**
	 * Updates the specified entity restricting to specified space identity.
	 * @param entity Entity to update.
	 * @param spaceIdentity Space identity to which update should be restricted.
	 * @return Success/failure
	 */
	public boolean updateByUserSpace(E entity, @Condition("spaceIdentity") String spaceIdentity);
	
	/**
	 * Deletes the entity restricting deletion to specified space identity.
	 * @param id Entity id to be deleted.
	 * @param spaceIdentity Space identity to which deletion should be restricted.
	 * @return Success/failure.
	 */
	public boolean deleteByIdAndUserSpace(@Condition("id") Object id, @Condition("spaceIdentity") String spaceIdentity);
}
