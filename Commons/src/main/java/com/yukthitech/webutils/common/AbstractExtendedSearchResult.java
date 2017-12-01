package com.yukthitech.webutils.common;

import java.util.HashMap;
import java.util.Map;

import com.yukthitech.persistence.repository.search.DynamicResultField;
import com.yukthitech.webutils.common.annotations.IgnoreField;

/**
 * Base class for extended search results model.
 * @author akiran
 */
public class AbstractExtendedSearchResult implements IExtendedSearchResult
{
	/**
	 * Map to store extended fields.
	 */
	@IgnoreField
	private Map<String, Object> extendedFields = new HashMap<>();

	/**
	 * Instantiates a new abstract extended search result.
	 */
	public AbstractExtendedSearchResult()
	{
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.persistence.repository.search.IDynamicSearchResult#addField(com.yukthitech.persistence.repository.search.DynamicResultField)
	 */
	@Override
	public void addField(DynamicResultField field)
	{
		extendedFields.put(field.getField(), field.getValue());
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.webutils.common.IExtendedSearchResult#getDynamicFieldValue(java.lang.String)
	 */
	@Override
	public Object getDynamicFieldValue(String name)
	{
		return extendedFields.get(name);
	}
}
