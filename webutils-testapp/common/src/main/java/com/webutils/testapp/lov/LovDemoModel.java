package com.webutils.testapp.lov;

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
 * Demo model for single editable-lov (CATEGORY) end-to-end persistence testing.
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
	@Description("Stored LOV used by editable-lov widget. Persisted to TEMP_TABLE on submit.")
	private String category;
}
