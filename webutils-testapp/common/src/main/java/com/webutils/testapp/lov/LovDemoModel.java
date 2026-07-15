package com.webutils.testapp.lov;

import java.util.List;

import com.webutils.common.form.annotations.Description;
import com.webutils.common.form.annotations.DynLovType;
import com.webutils.common.form.annotations.LOV;
import com.webutils.common.form.annotations.Label;
import com.webutils.common.form.annotations.Model;
import com.yukthitech.validation.annotations.MaxLen;
import com.yukthitech.validation.annotations.Required;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Demo model for editable-lov and multi-editable-lov widgets.
 */
@Data
@NoArgsConstructor
@Model(name = "LovDemoModel")
public class LovDemoModel
{
	@Label("Category")
	@LOV(name = "CATEGORY", type = DynLovType.STORED_TYPE)
	@Required
	@MaxLen(100)
	@Description("Stored LOV used by editable-lov widget.")
	private String category;

	@Label("Tags")
	@LOV(name = "TAG", type = DynLovType.STORED_TYPE, parentField = "category")
	@MaxLen(20)
	@Description("Child multi-value stored LOV for multi-editable-lov widget.")
	private List<
		@Required
		@MaxLen(100)
		String> tags;

	@Label("Notes")
	@MaxLen(500)
	private String notes;
}
