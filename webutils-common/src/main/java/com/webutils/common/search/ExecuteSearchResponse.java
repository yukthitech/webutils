package com.webutils.common.search;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.webutils.common.response.BaseResponse;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Grid response for execute-search action.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ExecuteSearchResponse extends BaseResponse
{
	private List<SearchColumn> searchColumns;
	private List<SearchRow> searchResults;
	private int pageNumber;
	private long totalCount;

	@JsonIgnore
	private List<Object> actualResults;

	public ExecuteSearchResponse(List<Object> actualResults)
	{
		this.actualResults = actualResults;
	}

	public void addSearchColumn(SearchColumn searchColumn)
	{
		if(searchColumns == null)
		{
			searchColumns = new ArrayList<>();
		}
		searchColumns.add(searchColumn);
	}

	public void addSearchResult(SearchRow row)
	{
		if(searchResults == null)
		{
			searchResults = new ArrayList<>();
		}
		searchResults.add(row);
	}
}
