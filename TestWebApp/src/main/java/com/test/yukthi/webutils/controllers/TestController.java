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

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.test.yukthi.webutils.Authorization;
import com.test.yukthi.webutils.SecurityRole;
import com.test.yukthi.webutils.mail.TestMailConfig1;
import com.test.yukthi.webutils.models.ITestController;
import com.test.yukthi.webutils.models.TestBean;
import com.test.yukthi.webutils.models.TestMailModel;
import com.yukthi.webutils.annotations.ActionName;
import com.yukthi.webutils.common.models.BaseResponse;
import com.yukthi.webutils.common.models.mails.EmailServerSettings;
import com.yukthi.webutils.controllers.BaseController;
import com.yukthi.webutils.mail.EmailData;
import com.yukthi.webutils.mail.EmailService;

/**
 * Test controller to test spring validation enablement
 * @author akiran
 */
@RestController
@RequestMapping("/test")
@ActionName("test")
public class TestController extends BaseController implements ITestController
{
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private EmailServerSettings emailServerSettings;
	
	/* (non-Javadoc)
	 * @see com.test.yukthi.webutils.controllers.ITestController#test(com.test.yukthi.webutils.models.TestBean)
	 */
	@Override
	@ResponseBody
	@RequestMapping("/test")
	@ActionName("test")
	public BaseResponse test(@Valid @RequestBody TestBean testBean)
	{
		return new BaseResponse(0, "Sucess - " + testBean.getName());
	}
	
	/* (non-Javadoc)
	 * @see com.test.yukthi.webutils.controllers.ITestController#secured1(com.test.yukthi.webutils.models.TestBean)
	 */
	@Override
	@ResponseBody
	@RequestMapping("/secured1")
	@ActionName("secured1")
	@Authorization(SecurityRole.PROJ_ADMIN)
	public BaseResponse secured1(@Valid @RequestBody TestBean testBean)
	{
		return new BaseResponse(0, "Sucess - " + testBean.getName());
	}

	/* (non-Javadoc)
	 * @see com.test.yukthi.webutils.controllers.ITestController#secured2(com.test.yukthi.webutils.models.TestBean)
	 */
	@Override
	@ResponseBody
	@RequestMapping("/secured2")
	@ActionName("secured2")
	@Authorization({SecurityRole.PROJ_ADMIN, SecurityRole.CLIENT_ADMIN})
	public BaseResponse secured2(@Valid @RequestBody TestBean testBean)
	{
		return new BaseResponse(0, "Sucess - " + testBean.getName());
	}
	
	/* (non-Javadoc)
	 * @see com.test.yukthi.webutils.controllers.ITestController#sendMail(com.test.yukthi.webutils.models.TestMailModel)
	 */
	@Override
	@ResponseBody
	@RequestMapping(value = "/sendMail", method = RequestMethod.POST)
	@ActionName("sendMail")
	public BaseResponse sendMail(@RequestBody TestMailModel model) throws Exception
	{
		EmailData email = new EmailData();
		email.setSubjectTemplate(model.getSubject());
		email.setContentTemplate(model.getContent());
		email.setToList(new String[]{model.getToId()});
		
		TestMailConfig1 config = new TestMailConfig1();
		config.setName("Tname");
		config.setAge(16);
		
		File file1 = File.createTempFile("test", ".txt");
		FileUtils.write(file1, model.getAttachment1());
		config.setAttachment1(file1);

		File file2 = File.createTempFile("test", ".txt");
		FileUtils.write(file1, model.getAttachment2());
		config.setAttachment2(file2);

		config.setAttachment3(model.getAttachment3());

		emailService.sendEmail(emailServerSettings, email, config);
		return new BaseResponse(0, "Success");
	}
}
