package com.webutils.common.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.webutils.common.form.model.FieldDef;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Column configuration within per-user search settings.
 */
@Data
@NoArgsConstructor
public class SearchSettingsColumn
{
	private String label;
	private boolean displayed;
	private boolean backend;
	private List<SearchField> fields;
	private boolean required;
	private int order;
	private SearchResultType searchResultType = SearchResultType.NONE;

	@JsonIgnore
	private FieldDef fieldDef;

	public SearchSettingsColumn(String label, boolean displayed, boolean backend, SearchField field)
	{
		this(label, displayed, backend, new ArrayList<>(Arrays.asList(field)));
	}

	public SearchSettingsColumn(String label, boolean displayed, boolean backend, List<SearchField> fields)
	{
		this.label = label;
		this.displayed = displayed;
		this.backend = backend;
		this.fields = fields;
	}

	public SearchSettingsColumn(String label, boolean displayed, boolean backend)
	{
		this(label, displayed, backend, (List<SearchField>) null);
	}

	@JsonIgnore
	public String getFieldName()
	{
		return fields.get(0).getField();
	}

	@JsonIgnore
	public String getPropertyName()
	{
		return fields.get(0).getPropertyName();
	}

	@Override
	public boolean equals(Object obj)
	{
		if(obj == this)
		{
			return true;
		}
		if(!(obj instanceof SearchSettingsColumn other))
		{
			return false;
		}
		return label.equals(other.label);
	}

	@Override
	public int hashCode()
	{
		return label.hashCode();
	}

	@Override
	public String toString()
	{
		return "SearchSettingsColumn[label=" + label + ", displayed=" + displayed + "]";
	}
}
