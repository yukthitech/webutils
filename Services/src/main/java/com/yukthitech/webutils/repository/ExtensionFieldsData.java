package com.yukthitech.webutils.repository;

import java.util.HashMap;
import java.util.Map;

import com.yukthitech.persistence.repository.annotations.Field;
import com.yukthitech.persistence.repository.search.DynamicResultField;
import com.yukthitech.persistence.repository.search.IDynamicSearchResult;

/**
 * Bean to fetch extension field values.
 * @author akiran
 */
public class ExtensionFieldsData implements IDynamicSearchResult
{
	/**
	 * Id of the entity.
	 */
	@Field("id")
	private long id;

	/**
	 * Extended field values.
	 */
	private Map<String, String> extendedFields = new HashMap<>();

	/**
	 * Gets the id of the entity.
	 *
	 * @return the id of the entity
	 */
	public long getId()
	{
		return id;
	}

	/**
	 * Sets the id of the entity.
	 *
	 * @param id the new id of the entity
	 */
	public void setId(long id)
	{
		this.id = id;
	}

	/**
	 * Gets the extended field values.
	 *
	 * @return the extended field values
	 */
	public Map<String, String> getExtendedFields()
	{
		return extendedFields;
	}

	/**
	 * Sets the extended field values.
	 *
	 * @param extendedFields the new extended field values
	 */
	public void setExtendedFields(Map<String, String> extendedFields)
	{
		this.extendedFields = extendedFields;
	}

	@Override
	public void addField(DynamicResultField field)
	{
		Object value = field.getValue();
		value = (value == null) ? null : value.toString();
		
		this.extendedFields.put(field.getField(), (String) value);
	}
}
