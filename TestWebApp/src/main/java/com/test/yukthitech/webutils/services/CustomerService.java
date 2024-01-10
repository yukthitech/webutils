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

package com.test.yukthitech.webutils.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.test.yukthitech.webutils.entity.CustomerEntity;
import com.test.yukthitech.webutils.entity.ICustomerRepository;
import com.yukthitech.persistence.repository.RepositoryFactory;

import jakarta.annotation.PostConstruct;

/**
 * @author akiran
 *
 */
@Service
public class CustomerService
{
	@Autowired
	private RepositoryFactory repositoryFactory;
	
	private ICustomerRepository customerRepository;
	
	@PostConstruct
	public void init()
	{
		customerRepository = repositoryFactory.getRepository(ICustomerRepository.class);
	}
	
	public CustomerEntity findByName(String name)
	{
		return customerRepository.findByName(name);
	}
	
	public void save(CustomerEntity customer)
	{
		customerRepository.save(customer);
	}
	
	public void deleteAll()
	{
		customerRepository.deleteAll();
	}
}
