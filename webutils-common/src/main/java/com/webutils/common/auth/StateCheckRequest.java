package com.webutils.common.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request passed to the app {@code IWebutilsService} when checking whether
 * an operation is allowed in the current user state.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StateCheckRequest
{
	private StateOperation operation;

	private String fieldId;

	private String value;
}
