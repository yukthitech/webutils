package com.yukthi.webutils.bootstrap;

import java.util.HashMap;
import java.util.Map;

/**
 * Context to maintain data loaded during boot straping.
 * @author akiran
 */
public class BootstrapLoadContext
{
	/**
	 * Map to maintain group wise entities.
	 */
	private Map<String, Object> entities = new HashMap<>();
	
	/**
	 * Adds specified entity under specified group with specified id.
	 * @param group Group to which entity needs to be added.
	 * @param id Identity of the entity.
	 * @param entity Entity to be added.
	 */
	public void addedEntity(String group, String id, Object entity)
	{
		entities.put(group + "." + id, entity);
	}
	
	/**
	 * Gets entity with specified (which should be combination of group and id value).
	 * @param entityId Entity id to search for.
	 * @return Matching entity.
	 */
	public Object getEntity(String entityId)
	{
		return entities.get(entityId);
	}
}
