package com.yukthitech.webutils.common.models.search;

/**
 * Field details of search column.
 * @author akiran
 */
public class SearchField
{
	/**
	 * Extension name, under which this field should be used.
	 */
	private String extensionName;
	
	/**
	 * Field name.
	 */
	private String field;
	
	private String propertyName;

	/**
	 * Instantiates a new search field.
	 *
	 * @param extensionName the extension name
	 * @param field the field
	 */
	public SearchField(String extensionName, String field, String propertyName)
	{
		this.extensionName = extensionName;
		this.field = field;
		this.propertyName = propertyName;
	}

	/**
	 * Gets the extension name, under which this field should be used.
	 *
	 * @return the extension name, under which this field should be used
	 */
	public String getExtensionName()
	{
		return extensionName;
	}

	/**
	 * Gets the field name.
	 *
	 * @return the field name
	 */
	public String getField()
	{
		return field;
	}
	
	public String getPropertyName()
	{
		return propertyName;
	}
}
