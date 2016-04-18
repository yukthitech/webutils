package com.yukthi.webutils.common;

import java.util.HashMap;
import java.util.Map;

import com.yukthi.persistence.repository.search.DynamicResultField;
import com.yukthi.webutils.common.annotations.IgnoreField;

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
	 * @see com.yukthi.persistence.repository.search.IDynamicSearchResult#addField(com.yukthi.persistence.repository.search.DynamicResultField)
	 */
	@Override
	public void addField(DynamicResultField field)
	{
		extendedFields.put(field.getField(), field.getValue());
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.common.IExtendedSearchResult#getDynamicFieldValue(java.lang.String)
	 */
	@Override
	public Object getDynamicFieldValue(String name)
	{
		return extendedFields.get(name);
	}
}
