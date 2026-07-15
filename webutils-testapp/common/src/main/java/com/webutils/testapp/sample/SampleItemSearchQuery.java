package com.webutils.testapp.sample;

import com.webutils.common.form.annotations.DynLovType;
import com.webutils.common.form.annotations.LOV;
import com.webutils.common.form.annotations.Label;
import com.webutils.common.form.annotations.Model;
import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.persistence.repository.annotations.Operator;

import lombok.Data;

/**
 * Search criteria for sampleItemSearch (yk-search-form).
 */
@Data
@Model(name = "SampleItemSearchQuery")
public class SampleItemSearchQuery
{
	@Label("Name")
	@Condition(value = "name", op = Operator.LIKE, ignoreCase = true)
	private String name;

	@Label("Category")
	@LOV(name = "CATEGORY", type = DynLovType.STORED_TYPE, persist = false)
	@Condition("category")
	private String category;

	@Label("Status")
	@Condition("status")
	private String status;
}
