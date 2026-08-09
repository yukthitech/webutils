package com.webutils.testapp.lov;

import com.webutils.common.form.annotations.Label;

/**
 * Sample enum for LOV widget demos (5 values so search enables when count &gt; 3).
 */
public enum DemoItemStatus
{
	@Label("Active")
	ACTIVE,

	@Label("Inactive")
	INACTIVE,

	@Label("Draft")
	DRAFT,

	@Label("Archived")
	ARCHIVED,

	@Label("Pending")
	PENDING;
}
