/*
 * The MIT License (MIT)
 * Copyright (c) 2016 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

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

package com.test.yukthitech.webutils.models;

import com.yukthitech.webutils.common.annotations.Model;

/**
 * @author akiran
 *
 */
@Model
public class TestMailModel
{
	private String subject;
	
	private String content;
	
	private String fromId;
	
	private String toId;
	
	private String attachment1;
	
	private String attachment2;
	
	private String attachment3;
	
	public TestMailModel()
	{}
	
	public TestMailModel(String subject, String content, String fromId)
	{
		this.subject = subject;
		this.content = content;
		this.fromId = fromId;
	}

	public String getSubject()
	{
		return subject;
	}

	public void setSubject(String subject)
	{
		this.subject = subject;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public String getFromId()
	{
		return fromId;
	}

	public void setFromId(String fromId)
	{
		this.fromId = fromId;
	}

	public String getToId()
	{
		return toId;
	}

	public void setToId(String toId)
	{
		this.toId = toId;
	}

	public String getAttachment1()
	{
		return attachment1;
	}

	public void setAttachment1(String attachment1)
	{
		this.attachment1 = attachment1;
	}

	public String getAttachment2()
	{
		return attachment2;
	}

	public void setAttachment2(String attachment2)
	{
		this.attachment2 = attachment2;
	}

	public String getAttachment3()
	{
		return attachment3;
	}

	public void setAttachment3(String attachment3)
	{
		this.attachment3 = attachment3;
	}
}
