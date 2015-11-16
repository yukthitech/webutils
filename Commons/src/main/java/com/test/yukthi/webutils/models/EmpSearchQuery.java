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

package com.test.yukthi.webutils.models;

import com.yukthi.persistence.repository.annotations.Condition;
import com.yukthi.persistence.repository.annotations.Operator;
import com.yukthi.webutils.common.annotations.Model;

/**
 * Emp search query
 * @author akiran
 */
@Model
public class EmpSearchQuery
{
	/**
	 * Employee search name pattern
	 */
	@Condition(value = "name", op = Operator.LIKE)
	private String name;
	
	/**
	 * Instantiates a new emp search query.
	 */
	public EmpSearchQuery()
	{}

	/**
	 * Instantiates a new emp search query.
	 *
	 * @param name the name
	 */
	public EmpSearchQuery(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the employee search name pattern.
	 *
	 * @return the employee search name pattern
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the employee search name pattern.
	 *
	 * @param name the new employee search name pattern
	 */
	public void setName(String name)
	{
		this.name = name;
	}
}
