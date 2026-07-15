package com.webutils.testapp.sample;

import com.webutils.common.form.annotations.Label;
import com.webutils.common.form.annotations.Model;
import com.webutils.common.form.annotations.NonDisplayable;
import com.webutils.common.form.annotations.SearchFieldInfo;
import com.yukthitech.persistence.repository.annotations.Field;
import com.yukthitech.validation.annotations.Required;

import lombok.Data;

/**
 * Search result row for sampleItemSearch (yk-search-results).
 */
@Data
@Model(name = "SampleItemSearchResult")
public class SampleItemSearchResult
{
	@Required
	@NonDisplayable(backend = true)
	@SearchFieldInfo(order = 0)
	@Field("id")
	private Long id;

	@Label("Name")
	@SearchFieldInfo(order = 1)
	@Field("name")
	private String name;

	@Label("Category")
	@SearchFieldInfo(order = 2)
	@Field("category")
	private String category;

	@Label("Status")
	@SearchFieldInfo(order = 3)
	@Field("status")
	private String status;

	@Label("Description")
	@SearchFieldInfo(order = 4)
	@Field("description")
	private String description;
}
