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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Generic response to return list of values.
 * @param <V> Type of value maintained.
 * @author akiran
 */
public class BasicReadListResponse<V> extends BaseResponse
{
	/**
	 * List of values.
	 */
	private List<V> values;
	
	/**
	 * Instantiates a new lov list response.
	 */
	public BasicReadListResponse()
	{}

	/**
	 * Instantiates a new value list response.
	 *
	 * @param values the value list
	 */
	public BasicReadListResponse(Collection<V> values)
	{
		this.values = new ArrayList<>(values);
	}

	/**
	 * Gets the list of values.
	 *
	 * @return the list of values
	 */
	public List<V> getValues()
	{
		return values;
	}

	/**
	 * Sets the list of values.
	 *
	 * @param values the new list of values
	 */
	public void setValues(List<V> values)
	{
		this.values = values;
	}
}
