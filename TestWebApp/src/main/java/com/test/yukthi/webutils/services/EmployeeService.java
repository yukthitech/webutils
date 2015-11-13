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

package com.test.yukthi.webutils.services;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.test.yukthi.webutils.entity.EmployeeEntity;
import com.test.yukthi.webutils.entity.IEmployeeRepository;
import com.yukthi.persistence.repository.RepositoryFactory;

/**
 * @author akiran
 *
 */
@Service
public class EmployeeService
{
	@Autowired
	private RepositoryFactory repositoryFactory;
	
	private IEmployeeRepository employeeRepository;
	
	@PostConstruct
	public void init()
	{
		employeeRepository = repositoryFactory.getRepository(IEmployeeRepository.class);
		
		//create test data
		employeeRepository.deleteAll();
		
		employeeRepository.save(new EmployeeEntity("Test1", 1000));
		employeeRepository.save(new EmployeeEntity("Test2", 4000));
		employeeRepository.save(new EmployeeEntity("Test3", 5000));
	}
	
	public void save(EmployeeEntity entity)
	{
		employeeRepository.save(entity);
	}
	
	public void update(EmployeeEntity entity)
	{
		employeeRepository.update(entity);
	}
	
	public EmployeeEntity fetch(long id)
	{
		return employeeRepository.findById(id);
	}
	
	public void deleteAll()
	{
		employeeRepository.deleteAll();
	}
}
