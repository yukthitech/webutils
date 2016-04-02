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

package com.test.yukthi.webutils.entity;

import javax.persistence.Column;
import javax.persistence.Table;

import com.yukthi.webutils.annotations.ExtendableEntity;
import com.yukthi.webutils.repository.WebutilsEntity;

/**
 * Test entity
 * 
 * @author akiran
 */
@ExtendableEntity(name = "Employee")
@Table(name = "EMP")
public class EmployeeEntity extends WebutilsEntity
{
	/**
	 * Name of the employee
	 */
	@Column(name = "NAME")
	private String name;

	/**
	 * Salary of the employee
	 */
	@Column(name = "SALARY")
	private long salary;
	
	/**
	 * Instantiates a new employee entity.
	 */
	public EmployeeEntity()
	{}

	/**
	 * Instantiates a new employee entity.
	 *
	 * @param name the name
	 * @param salary the salary
	 */
	public EmployeeEntity(String name, long salary)
	{
		this.name = name;
		this.salary = salary;
	}

	/**
	 * Gets the name of the employee.
	 *
	 * @return the name of the employee
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the employee.
	 *
	 * @param name the new name of the employee
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the salary of the employee.
	 *
	 * @return the salary of the employee
	 */
	public long getSalary()
	{
		return salary;
	}

	/**
	 * Sets the salary of the employee.
	 *
	 * @param salary the new salary of the employee
	 */
	public void setSalary(long salary)
	{
		this.salary = salary;
	}
}
