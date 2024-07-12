package com.yukthitech.webutils.lov;

import com.yukthitech.webutils.common.lov.LovType;

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
	
	private LovType lovType;

	public LovRef(String name, String fieldName, LovType lovType)
	{
		this.name = name;
		this.fieldName = fieldName;
		this.lovType = lovType;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getFieldName()
	{
		return fieldName;
	}
	
	public LovType getLovType()
	{
		return lovType;
	}
}
