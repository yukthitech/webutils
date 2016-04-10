package com.yukthi.webutils.repository;

import java.util.HashMap;
import java.util.Map;

import com.yukthi.persistence.annotations.ExtendedFields;

/**
 * Base class for extendable entities.
 * @author akiran
 */
public class WebutilsExtendableEntity extends WebutilsEntity
{
	/**
	 * Map to store extended fields data.
	 */
	@ExtendedFields
	private Map<String, String> extendedFields;

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
