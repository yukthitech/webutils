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
public interface IWebutilsRepository<E extends WebutilsBaseEntity> extends ICrudRepository<E>
{
	/**
	 * Fetches extended fields for specified entity id.
	 * @param id Entity id for which extended fields needs to be fetched.
	 * @param fieldNames Extended field names to fetch
	 * @return Extended fields of the entity.
	 */
	@SearchResult
	public ExtensionFieldsData fetchExtendedFields(@Condition("id") Object id, @ExtendedFieldNames Set<String> fieldNames);
}
