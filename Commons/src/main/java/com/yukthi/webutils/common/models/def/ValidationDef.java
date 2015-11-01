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

import java.util.Map;

/**
 * Indicates validation on a model field. That can be used on client side to
 * generate dynamic validations.
 * 
 * @author akiran
 */
public class ValidationDef
{
	/**
	 * Name of the validator. Validators on the client should match with this
	 * name.
	 */
	private String name;

	/**
	 * Internalized error message to be used in case validation fails. Will have
	 * expressions using ${} pattern. ${value} should hold the user input value
	 */
	private String errorMessage;
	
	/**
	 * Indicates this is cross validation (validates against other field)
	 */
	private boolean crossValidation;

	/**
	 * Value map that can be used to specify parameters for validator
	 */
	private Map<String, Object> values;

	/**
	 * Gets the name of the validator.
	 *
	 * @return the name of the validator
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the validator.
	 *
	 * @param name the new name of the validator
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the internalized error message to be used in case validation fails.
	 *
	 * @return the internalized error message to be used in case validation fails
	 */
	public String getErrorMessage()
	{
		return errorMessage;
	}

	/**
	 * Sets the internalized error message to be used in case validation fails.
	 *
	 * @param errorMessage the new internalized error message to be used in case validation fails
	 */
	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}

	/**
	 * Gets the value map that can be used to specify parameters for validator.
	 *
	 * @return the value map that can be used to specify parameters for validator
	 */
	public Map<String, Object> getValues()
	{
		return values;
	}

	/**
	 * Sets the value map that can be used to specify parameters for validator.
	 *
	 * @param values the new value map that can be used to specify parameters for validator
	 */
	public void setValues(Map<String, Object> values)
	{
		this.values = values;
	}

	/**
	 * Checks if is indicates this is cross validation (validates against other field).
	 *
	 * @return the indicates this is cross validation (validates against other field)
	 */
	public boolean isCrossValidation()
	{
		return crossValidation;
	}

	/**
	 * Sets the indicates this is cross validation (validates against other field).
	 *
	 * @param crossValidation the new indicates this is cross validation (validates against other field)
	 */
	public void setCrossValidation(boolean crossValidation)
	{
		this.crossValidation = crossValidation;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Name: ").append(name);
		builder.append(",").append("is Cross Val: ").append(crossValidation);
		builder.append(",").append("Values: ").append(values);
		
		builder.append("]");
		return builder.toString();
	}

}
