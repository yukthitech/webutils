package com.webutils.common.search;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webutils.common.form.annotations.Model;
import com.yukthitech.utils.exceptions.InvalidStateException;

import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Parameters for executing a search query.
 */
@Model
@Data
@NoArgsConstructor
public class SearchExecutionModel
{
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private String queryModelJson;

	private int pageSize = -1;

	@Min(1)
	private int pageNumber = 1;

	private boolean fetchCount = false;

	@JsonIgnore
	private boolean fetchAll = false;

	public SearchExecutionModel(int pageNumber, boolean fetchCount, boolean fetchAll)
	{
		this.pageNumber = pageNumber;
		this.fetchCount = fetchCount;
		this.fetchAll = fetchAll;
	}

	public void setQueryModel(Object model)
	{
		try
		{
			this.queryModelJson = OBJECT_MAPPER.writeValueAsString(model);
		}
		catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while converting specified object into json", ex);
		}
	}
}
