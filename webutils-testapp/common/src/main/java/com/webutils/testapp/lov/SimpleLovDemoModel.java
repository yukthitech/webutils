package com.webutils.testapp.lov;

import com.webutils.common.form.annotations.Description;
import com.webutils.common.form.annotations.DynLovType;
import com.webutils.common.form.annotations.LOV;
import com.webutils.common.form.annotations.Label;
import com.webutils.common.form.annotations.Model;
import com.yukthitech.validation.annotations.Required;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Demo model for simple (id-based) LOV field using yk-lov-field.
 */
@Data
@NoArgsConstructor
@Model(name = "SimpleLovDemoModel")
public class SimpleLovDemoModel
{
	@Label("Category")
	@LOV(name = "CATEGORY", type = DynLovType.STORED_TYPE)
	@Required
	@Description("Stored LOV option id selected via simple lov widget. Label is persisted to TEMP_TABLE on submit.")
	private Long categoryId;
}
