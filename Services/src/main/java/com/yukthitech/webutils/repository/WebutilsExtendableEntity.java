package com.yukthitech.webutils.repository;

import java.util.HashMap;
import java.util.Map;

import com.yukthitech.persistence.annotations.ExtendedFields;

/**
 * Base class for extendable entities.
 * @author akiran
 */
public class WebutilsExtendableEntity extends WebutilsBaseEntity
{
	/**
	 * Map to store extended fields data.
	 */
	@ExtendedFields
	private Map<String, String> extendedFields;

	/**
	 * Instantiates a new webutils extendable entity.
	 */
	public WebutilsExtendableEntity()
	{
		super();
	}

	/**
	 * Instantiates a new webutils extendable entity.
	 *
	 * @param id the id
	 */
	public WebutilsExtendableEntity(Long id)
	{
		super(id);
	}

	/**
	 * Gets the map to store extended fields data.
	 *
	 * @return the map to store extended fields data
	 */
	public Map<String, String> getExtendedFields()
	{
		return extendedFields;
	}

	/**
	 * Sets the map to store extended fields data.
	 *
	 * @param extendedFields the new map to store extended fields data
	 */
	public void setExtendedFields(Map<String, String> extendedFields)
	{
		this.extendedFields = extendedFields;
	}
	
	/**
	 * Adds extended field with specified name and value.
	 * @param name Name of the extended field.
	 * @param value Value of the extended field.
	 */
	public void addExtendedField(String name, String value)
	{
		if(extendedFields == null)
		{
			extendedFields = new HashMap<>();
		}
		
		extendedFields.put(name, value);
	}
}
