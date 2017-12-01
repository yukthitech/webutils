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

package com.yukthitech.webutils.common.models;

import java.util.List;

/**
 * Response representing LOV list
 * @author akiran
 */
public class LovListResponse extends BaseResponse
{
	/**
	 * List of lov values
	 */
	private List<ValueLabel> lovList;
	
	/**
	 * Instantiates a new lov list response.
	 */
	public LovListResponse()
	{}

	/**
	 * Instantiates a new lov list response.
	 *
	 * @param lovList the lov list
	 */
	public LovListResponse(List<ValueLabel> lovList)
	{
		this.lovList = lovList;
	}
	
	/**
	 * Sets the list of lov values.
	 *
	 * @param lovList the new list of lov values
	 */
	public void setLovList(List<ValueLabel> lovList)
	{
		this.lovList = lovList;
	}
	
	/**
	 * Gets the list of lov values.
	 *
	 * @return the list of lov values
	 */
	public List<ValueLabel> getLovList()
	{
		return lovList;
	}
}
