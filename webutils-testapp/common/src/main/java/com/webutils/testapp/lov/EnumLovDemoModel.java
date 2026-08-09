package com.webutils.testapp.lov;

import java.util.List;

import com.webutils.common.form.annotations.Description;
import com.webutils.common.form.annotations.Label;
import com.webutils.common.form.annotations.Model;
import com.yukthitech.validation.annotations.Required;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Demo model for enum single and multi LOV (yk-lov-field).
 */
@Data
@NoArgsConstructor
@Model(name = "EnumLovDemoModel")
public class EnumLovDemoModel
{
	@Label("Status")
	@Required
	@Description("Single enum LOV (STATIC).")
	private DemoItemStatus status;

	@Label("Statuses")
	@Required
	@Description("Multi enum LOV (STATIC, no create).")
	private List<DemoItemStatus> statuses;
}
