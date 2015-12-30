/*
 * The MIT License (MIT)
 * Copyright (c) 2015 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.yukthi.webutils.common.models.def;

import com.yukthi.webutils.common.LovType;

/**
 * LOV details for lov field
 * @author akiran
 */
public class LovDetails
{
	/**
	 * Type of lov
	 */
	private LovType lovType;
	
	/**
	 * Name of the lov
	 */
	private String lovName;
	
	/**
	 * Parent field, based on whose value current field's lov needs to be fetched. 
	 * Like State list based on country.
	 */
	private String parentField;
	
	/**
	 * Actual java underlying type of lov
	 */
	private FieldType actualType;

	/**
	 * Gets the type of lov.
	 *
	 * @return the type of lov
	 */
	public LovType getLovType()
	{
		return lovType;
	}

	/**
	 * Sets the type of lov.
	 *
	 * @param lovType the new type of lov
	 */
	public void setLovType(LovType lovType)
	{
		this.lovType = lovType;
	}

	/**
	 * Gets the name of the lov.
	 *
	 * @return the name of the lov
	 */
	public String getLovName()
	{
		return lovName;
	}

	/**
	 * Sets the name of the lov.
	 *
	 * @param lovName the new name of the lov
	 */
	public void setLovName(String lovName)
	{
		this.lovName = lovName;
	}

	/**
	 * Gets the parent field, based on whose value current field's lov needs to be fetched.
	 *
	 * @return the parent field, based on whose value current field's lov needs to be fetched
	 */
	public String getParentField()
	{
		return parentField;
	}

	/**
	 * Sets the parent field, based on whose value current field's lov needs to be fetched.
	 *
	 * @param parentField the new parent field, based on whose value current field's lov needs to be fetched
	 */
	public void setParentField(String parentField)
	{
		this.parentField = parentField;
	}
	
	/**
	 * Gets the actual java underlying type of lov.
	 *
	 * @return the actual java underlying type of lov
	 */
	public FieldType getActualType()
	{
		return actualType;
	}

	/**
	 * Sets the actual java underlying type of lov.
	 *
	 * @param actualType the new actual java underlying type of lov
	 */
	public void setActualType(FieldType actualType)
	{
		this.actualType = actualType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Type: ").append(lovType);
		builder.append(",").append("Name: ").append(lovName);
		builder.append(",").append("Parent Fld: ").append(parentField);

		builder.append("]");
		return builder.toString();
	}

}
