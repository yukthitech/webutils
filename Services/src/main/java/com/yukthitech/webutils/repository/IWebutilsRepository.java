package com.yukthitech.webutils.repository;

import java.util.Set;

import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.persistence.repository.annotations.ExtendedFieldNames;
import com.yukthitech.persistence.repository.annotations.SearchResult;

/**
 * Defines common repository methods with limitations on space.
 * @author akiran
 * @param <E> Entity type
 */
public interface IWebutilsRepository<E extends WebutilsEntity> extends ICrudRepository<E>
{
	/**
	 * Fetches entity based on id and restricting to specified space identity.
	 * @param id entity id.
	 * @param spaceIdentity Space identity string to which search should be restricted.
	 * @return Matching entity.
	 */
	public E findByIdAndUserSpace(@Condition("id") Object id, @Condition("spaceIdentity") String spaceIdentity);
	
	/**
	 * Fetches extended fields for specified entity id.
	 * @param id Entity id for which extended fields needs to be fetched.
	 * @param fieldNames Extended field names to fetch
	 * @return Extended fields of the entity.
	 */
	@SearchResult
	public ExtensionFieldsData fetchExtendedFields(@Condition("id") Object id, @ExtendedFieldNames Set<String> fieldNames);
	
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
