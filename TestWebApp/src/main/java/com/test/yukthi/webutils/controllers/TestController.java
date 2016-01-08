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
package com.test.yukthi.webutils.controllers;

import java.io.File;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.test.yukthi.webutils.Authorization;
import com.test.yukthi.webutils.SecurityRole;
import com.test.yukthi.webutils.models.TestBean;
import com.test.yukthi.webutils.models.TestMailBean;
import com.test.yukthi.webutils.models.TestMailModel;
import com.yukthi.webutils.annotations.ActionName;
import com.yukthi.webutils.common.models.BaseResponse;
import com.yukthi.webutils.controllers.BaseController;
import com.yukthi.webutils.mail.EmailData;
import com.yukthi.webutils.mail.EmailService;
import com.yukthi.webutils.mail.EmailTemplateService;
import com.yukthi.webutils.mail.FileAttachment;

/**
 * Test controller to test spring validation enablement
 * @author akiran
 */
@RestController
@RequestMapping("/test")
@ActionName("test")
public class TestController extends BaseController
{
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private EmailTemplateService emailTemplateService;
	
	/**
	 * Simple test control method which is used by client test cases to 
	 * check for spring validation enabling.
	 * @param testBean
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/test")
	@ActionName("test")
	public BaseResponse test(@Valid @RequestBody TestBean testBean)
	{
		return new BaseResponse(0, "Sucess - " + testBean.getName());
	}
	
	@ResponseBody
	@RequestMapping("/secured1")
	@ActionName("secured1")
	@Authorization(SecurityRole.PROJ_ADMIN)
	public BaseResponse secured1(@Valid @RequestBody TestBean testBean)
	{
		return new BaseResponse(0, "Sucess - " + testBean.getName());
	}

	@ResponseBody
	@RequestMapping("/secured2")
	@ActionName("secured2")
	@Authorization({SecurityRole.PROJ_ADMIN, SecurityRole.CLIENT_ADMIN})
	public BaseResponse secured2(@Valid @RequestBody TestBean testBean)
	{
		return new BaseResponse(0, "Sucess - " + testBean.getName());
	}
	
	@ResponseBody
	@RequestMapping(value = "/sendMail", method = RequestMethod.POST)
	@ActionName("sendMail")
	public BaseResponse sendMail(@RequestBody TestMailModel model)
	{
		EmailData email = new EmailData();
		email.setSubject(model.getSubject());
		email.setContent(model.getContent());
		email.setFromId(model.getFromId());
		email.setToList(new String[]{model.getToId()});
		
		if(model.getAttachments() != null)
		{
			int idx = 0;
			FileAttachment mailAttachments[] = new FileAttachment[model.getAttachments().length];
			
			for(String attachFile : model.getAttachments())
			{
				mailAttachments[idx] = new FileAttachment(new File(attachFile), "Test" + idx + ".txt"); 
				idx++;
			}
			
			email.setAttachments(mailAttachments);
		}
		
		emailService.sendEmail(email);
		return new BaseResponse(0, "Success");
	}
	
	@ResponseBody
	@RequestMapping(value = "/sendMailByTemplate", method = RequestMethod.POST)
	@ActionName("sendMailByTemplate")
	public BaseResponse sendMailByTemplate(@RequestBody TestMailBean model)
	{
		EmailData email = emailTemplateService.getEmailTemplate("test").toEmailData(model);
		email.setToList(new String[]{model.getToMailId()});
		
		emailService.sendEmail(email);
		return new BaseResponse(0, "Success");
	}
}
