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
package com.yukthi.webutils.common.models;

/**
 * Value label pair. Mainly used to pass LOV data to client
 * @author akiran
 */
public class ValueLabel
{
	/**
	 * Value of the item
	 */
	private String value;
	
	/**
	 * Label for the item, that can be used in client ui.
	 */
	private String label;
	
	/**
	 * Instantiates a new value label.
	 */
	public ValueLabel()
	{
	}

	/**
	 * Instantiates a new value label.
	 *
	 * @param value the value
	 * @param label the label
	 */
	public ValueLabel(String value, String label)
	{
		this.value = value;
		this.label = label;
	}

	/**
	 * Gets the value of the item.
	 *
	 * @return the value of the item
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * Sets the value of the item.
	 *
	 * @param value the new value of the item
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

	/**
	 * Gets the label for the item, that can be used in client ui.
	 *
	 * @return the label for the item, that can be used in client ui
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * Sets the label for the item, that can be used in client ui.
	 *
	 * @param label the new label for the item, that can be used in client ui
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Value: ").append(value);
		builder.append(",").append("Lable: ").append(label);

		builder.append("]");
		return builder.toString();
	}
}
