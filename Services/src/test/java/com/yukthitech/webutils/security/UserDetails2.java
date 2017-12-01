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

package com.yukthitech.webutils.security;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yukthitech.webutils.security.UserDetails;

/**
 * Test user details with security fields
 * 
 * @author akiran
 */
public class UserDetails2 extends UserDetails
{
	private int field1;

	private long field2;

	private Set<TestRole> roles;

	public UserDetails2()
	{}

	public UserDetails2(int field1, long field2)
	{
		this.field1 = field1;
		this.field2 = field2;
	}

	@JsonProperty("f1")
	public int getField1()
	{
		return field1;
	}

	public void setField1(int field1)
	{
		this.field1 = field1;
	}

	@JsonProperty("f2")
	public long getField2()
	{
		return field2;
	}

	public void setField2(long field2)
	{
		this.field2 = field2;
	}

	@JsonProperty("ro")
	public Set<TestRole> getRoles()
	{
		return roles;
	}

	public void setRoles(Set<TestRole> roles)
	{
		this.roles = roles;
	}

}
