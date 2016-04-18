/*
 * 
 */

package com.yukthi.webutils.common.models.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Represents a search column settings for search settings.
 * @author akiran
 */
public class SearchSettingsColumn
{
	/**
	 * Name of the column.
	 */
	private String name;
	
	/**
	 * Flag indicating whether column should be displayed or not.
	 */
	private boolean displayed;
	
	/**
	 * Specifies whether this is an extended field.
	 */
	private boolean extended;
	
	/**
	 * Entity fields that needs to be used to fetch value for this column.
	 */
	private List<SearchField> fields;
	
	/**
	 * Indicates if this is required field or not.
	 */
	private boolean required;

	/**
	 * Instantiates a new search settings column full.
	 *
	 * @param name the name
	 * @param displayed the displayed
	 * @param extended the extended
	 * @param fields the fields
	 */
	public SearchSettingsColumn(String name, boolean displayed, boolean extended, List<SearchField> fields)
	{
		this.name = name;
		this.displayed = displayed;
		this.extended = extended;
		this.fields = fields;
	}

	/**
	 * Instantiates a new search settings column.
	 *
	 * @param name the name
	 * @param displayed the displayed
	 * @param extended is extended field
	 * @param field the field
	 */
	public SearchSettingsColumn(String name, boolean displayed, boolean extended, SearchField field)
	{
		this(name, displayed, extended, new ArrayList<>(Arrays.asList(field)) );
	}
	
	/**
	 * Instantiates a new search settings column full.
	 *
	 * @param name the name
	 * @param displayed the displayed
	 */
	public SearchSettingsColumn(String name, boolean displayed)
	{
		this(name, displayed, false, (List<SearchField>) null);
	}

	/**
	 * Gets the entity fields that needs to be used to fetch value for this column.
	 *
	 * @return the entity fields that needs to be used to fetch value for this column
	 */
	public List<SearchField> getFields()
	{
		return fields;
	}

	/**
	 * Sets the entity fields that needs to be used to fetch value for this column.
	 *
	 * @param fields the new entity fields that needs to be used to fetch value for this column
	 */
	public void setFields(List<SearchField> fields)
	{
		this.fields = fields;
	}
	
	/**
	 * Gets the name of the column.
	 *
	 * @return the name of the column
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the column.
	 *
	 * @param name the new name of the column
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the flag indicating whether column should be displayed or not.
	 *
	 * @return the flag indicating whether column should be displayed or not
	 */
	public boolean isDisplayed()
	{
		return displayed;
	}

	/**
	 * Sets the flag indicating whether column should be displayed or not.
	 *
	 * @param displayed the new flag indicating whether column should be displayed or not
	 */
	public void setDisplayed(boolean displayed)
	{
		this.displayed = displayed;
	}

	/**
	 * Gets the specifies whether this is an extended field.
	 *
	 * @return the specifies whether this is an extended field
	 */
	public boolean isExtended()
	{
		return extended;
	}

	/**
	 * Sets the specifies whether this is an extended field.
	 *
	 * @param extended the new specifies whether this is an extended field
	 */
	public void setExtended(boolean extended)
	{
		this.extended = extended;
	}

	/**
	 * Gets the indicates if this is required field or not.
	 *
	 * @return the indicates if this is required field or not
	 */
	@JsonIgnore
	public boolean isRequired()
	{
		return required;
	}

	/**
	 * Sets the indicates if this is required field or not.
	 *
	 * @param required the new indicates if this is required field or not
	 */
	public void setRequired(boolean required)
	{
		this.required = required;
	}
	
	/**
	 * Fetches the associated field name.
	 * @return Field name
	 */
	@JsonIgnore
	public String getFieldName()
	{
		return fields.get(0).getField();
	}
	
	/**
	 * Fetches the mixed field name matching with specified extension name.
	 * @param extensionName Extension name
	 * @return Matching field name
	 */
	public String getMixedFieldName(String extensionName)
	{
		for(SearchField field : fields)
		{
			if(extensionName.equals(field.getExtensionName()))
			{
				return field.getField();
			}
		}
		
		return null;
	}
	
	/**
	 * Returns true if the multiple fields are associated with same column. This would be the case
	 * when different extension use same label for different extended columns.
	 * @return true if mixed field.
	 */
	@JsonIgnore
	public boolean isMixedField()
	{
		return fields.size() > 1;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(obj == this)
		{
			return true;
		}

		if(!(obj instanceof SearchSettingsColumn))
		{
			return false;
		}

		SearchSettingsColumn other = (SearchSettingsColumn) obj;
		return name.equals(other.name) && (extended == other.extended);
	}

	/**
	 * Hash code.
	 *
	 * @return the int
	 */
	/* (non-Javadoc)
	 * @see java.lang.Object#hashcode()
	 */
	@Override
	public int hashCode()
	{
		return name.hashCode() + (extended ? 1 : 0);
	}
}
