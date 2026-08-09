package com.webutils.testapp.lov;

import java.util.List;

import com.webutils.common.form.annotations.Description;
import com.webutils.common.form.annotations.DynLovType;
import com.webutils.common.form.annotations.LOV;
import com.webutils.common.form.annotations.Label;
import com.webutils.common.form.annotations.Model;
import com.yukthitech.validation.annotations.Required;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Demo model for multi-editable-lov (CATEGORY labels) end-to-end persistence testing.
 */
@Data
@NoArgsConstructor
@Model(name = "MultiEditableLovDemoModel")
public class MultiEditableLovDemoModel
{
	@Label("Categories")
	@LOV(name = "CATEGORY", type = DynLovType.STORED_TYPE)
	@Required
	@Description("Stored LOV multi-select used by multi-editable-lov widget. Persisted to TEMP_TABLE.CATEGORIES on submit.")
	private List<String> categories;

	@Label("Category filters")
	@LOV(name = "CATEGORY", type = DynLovType.STORED_TYPE, persist = false)
	@Description("Multi select-only String LOV (persist=false). New values cannot be added in UI.")
	private List<String> categoryFilters;
}
