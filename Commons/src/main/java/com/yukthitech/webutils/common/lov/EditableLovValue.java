package com.yukthitech.webutils.common.lov;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yukthitech.validation.IStringConvertible;
import com.yukthitech.webutils.common.annotations.Model;

import jakarta.validation.constraints.Min;

/**
 * Type to be used to accept value from editable lov field.
 */
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
	 * Gets the id of the selected lov.
	 *
	 * @return the id of the selected lov
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * Sets the id of the selected lov.
	 *
	 * @param id the new id of the selected lov
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * Gets the new value fed for the field.
	 *
	 * @return the new value fed for the field
	 */
	public String getNewValue()
	{
		return newValue;
	}

	/**
	 * Sets the new value fed for the field.
	 *
	 * @param newValue the new new value fed for the field
	 */
	public void setNewValue(String newValue)
	{
		this.newValue = newValue;
	}
	
	@Override
	public String toStringValue()
	{
		return newValue;
	}
}
