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

import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yukthitech.webutils.repository.IUserRepository;
import com.yukthitech.webutils.repository.UserEntity;
import com.yukthitech.webutils.services.WebutilsRepositoryFactory;

/**
 * @author akiran
 *
 */
@Service
public class TestUserService
{
	@Autowired
	private WebutilsRepositoryFactory repositoryFactory;
	
	private long userId;
	
	@PostConstruct
	private void init()
	{
		IUserRepository userRepository = repositoryFactory.getRepository(IUserRepository.class);
		
		UserEntity user = userRepository.fetchUser("admin");
		
		if(user != null)
		{
			this.userId = user.getId();
			return;
		}
		
		user = new UserEntity("admin", "admin", "admin");
		user.setVersion(1);
		user.setSpaceIdentity("admin");
		user.setUpdatedOn(new Date());
		user.setCreatedOn(new Date());
		
		userRepository.save(user);
		
		this.userId = user.getId();
	}
	
	/**
	 * @return the {@link #userId userId}
	 */
	public long getUserId()
	{
		return userId;
	}
}
