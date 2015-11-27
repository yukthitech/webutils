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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import com.yukthi.webutils.annotations.ExtendableEntity;
import com.yukthi.webutils.repository.ITrackedEntity;

/**
 * Test entity
 * 
 * @author akiran
 */
@ExtendableEntity(name = "Employee")
@Table(name = "EMP")
public class EmployeeEntity implements ITrackedEntity
{
	/**
	 * Employee id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	/** The version. */
	@Version
	private Integer version;
	
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
	
	/** The created on. */
	private Date createdOn;
	
	/** The created by. */
	private Long createdBy;
	
	/** The updated on. */
	private Date updatedOn;
	
	/** The updated by. */
	private Long updatedBy;

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

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.IEntity#getId()
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * Sets the employee id.
	 *
	 * @param id the new employee id
	 */
	public void setId(Long id)
	{
		this.id = id;
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

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.repository.ITrackedEntity#getCreatedOn()
	 */
	public Date getCreatedOn()
	{
		return createdOn;
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.repository.ITrackedEntity#setCreatedOn(java.util.Date)
	 */
	public void setCreatedOn(Date createdOn)
	{
		this.createdOn = createdOn;
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.repository.ITrackedEntity#getCreatedBy()
	 */
	public Long getCreatedBy()
	{
		return createdBy;
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.repository.ITrackedEntity#setCreatedBy(java.lang.Long)
	 */
	public void setCreatedBy(Long createdBy)
	{
		this.createdBy = createdBy;
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.repository.ITrackedEntity#getUpdatedOn()
	 */
	public Date getUpdatedOn()
	{
		return updatedOn;
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.repository.ITrackedEntity#setUpdatedOn(java.util.Date)
	 */
	public void setUpdatedOn(Date updatedOn)
	{
		this.updatedOn = updatedOn;
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.repository.ITrackedEntity#getUpdatedBy()
	 */
	public Long getUpdatedBy()
	{
		return updatedBy;
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.repository.ITrackedEntity#setUpdatedBy(java.lang.Long)
	 */
	public void setUpdatedBy(Long updatedBy)
	{
		this.updatedBy = updatedBy;
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.IEntity#getVersion()
	 */
	public Integer getVersion()
	{
		return version;
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.IEntity#setVersion(java.lang.Integer)
	 */
	public void setVersion(Integer version)
	{
		this.version = version;
	}

	
}
