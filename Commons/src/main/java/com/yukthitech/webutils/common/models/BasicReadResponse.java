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

/**
 * Generic read response.
 * 
 * @author akiran
 */
public class BasicReadResponse<M> extends BaseResponse
{
	/**
	 * Model read from server.
	 */
	private M model;
	
	/**
	 * Instantiates a new basic read response.
	 */
	public BasicReadResponse()
	{}

	/**
	 * Instantiates a new basic read response.
	 *
	 * @param model the model
	 */
	public BasicReadResponse(M model)
	{
		this.model = model;
	}

	/**
	 * Gets the model read from server.
	 *
	 * @return the model read from server
	 */
	public M getModel()
	{
		return model;
	}

	/**
	 * Sets the model read from server.
	 *
	 * @param model the new model read from server
	 */
	public void setModel(M model)
	{
		this.model = model;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Model: ").append(model);
		builder.append(",").append("Code: ").append(super.getCode());

		builder.append("]");
		return builder.toString();
	}

}
