package com.webutils.common.search;

import java.util.ArrayList;
import java.util.List;

import com.webutils.common.form.annotations.IgnoreField;
import com.webutils.common.form.annotations.Model;
import com.yukthitech.validation.annotations.MinLen;
import com.yukthitech.validation.annotations.Required;

import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API model for persisting search settings.
 */
@Model
@Data
@NoArgsConstructor
public class SearchSettingsModel
{
	private Long id;
	private Integer version;

	@Required
	@MinLen(1)
	private String searchQueryName;

	@IgnoreField
	@Required
	private List<SearchSettingsColumn> searchColumns;

	@Min(1)
	private int pageSize;

	public void addSearchColumn(SearchSettingsColumn column)
	{
		if(searchColumns == null)
		{
			searchColumns = new ArrayList<>();
		}
		searchColumns.add(column);
	}
}
