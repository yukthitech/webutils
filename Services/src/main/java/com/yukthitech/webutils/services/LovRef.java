package com.yukthitech.webutils.services;

/**
 * Lov reference.
 * @author akiran
 */
public class LovRef
{
	/**
	 * Name of the lov name.
	 */
	private String name;
	
	/**
	 * Name of the field.
	 */
	private String fieldName;

	public LovRef(String name, String fieldName)
	{
		this.name = name;
		this.fieldName = fieldName;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getFieldName()
	{
		return fieldName;
	}
}
