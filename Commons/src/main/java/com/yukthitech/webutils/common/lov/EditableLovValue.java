package com.yukthitech.webutils.common.lov;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yukthitech.validation.IStringConvertible;
import com.yukthitech.webutils.common.annotations.Model;

import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Type to be used to accept value from editable lov field.
 */
@Data
@Accessors(chain = true)
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
public class EditableLovValue implements IStringConvertible
{
	/**
	 * Id of the selected lov.
	 */
	@Min(1)
	private Long id;
	
	/**
	 * New value fed for the field.
	 */
	private String newValue;
	
	/**
	 * This field is populated during read from the server.
	 * This field will not be considered during persistence operations.
	 */
	private String label;

	@Override
	public String toStringValue()
	{
		return newValue;
	}
}
