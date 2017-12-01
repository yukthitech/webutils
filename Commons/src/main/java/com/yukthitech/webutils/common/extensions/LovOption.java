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

package com.yukthitech.webutils.common.extensions;

/**
 * LOV option that can be used for extension field
 * @author akiran
 */
public class LovOption
{
	/**
	 * Value of the LOV option
	 */
	private String value;

	/**
	 * Label of the LOV option
	 */
	private String label;
	
	/**
	 * Instantiates a new lov option.
	 */
	public LovOption()
	{}
	
	/**
	 * Instantiates a new lov option.
	 *
	 * @param value the value
	 * @param label the label
	 */
	public LovOption(String value, String label)
	{
		this.value = value;
		this.label = label;
	}

	/**
	 * Gets the value of the LOV option.
	 *
	 * @return the value of the LOV option
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * Sets the value of the LOV option.
	 *
	 * @param value the new value of the LOV option
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

	/**
	 * Gets the label of the LOV option.
	 *
	 * @return the label of the LOV option
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * Sets the label of the LOV option.
	 *
	 * @param label the new label of the LOV option
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}

}
