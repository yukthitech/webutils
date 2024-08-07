/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yukthitech.webutils.verification;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.webutils.common.verification.VerificationType;
import com.yukthitech.webutils.mail.EmailService;

@Service
public class EmailVerificationSupport extends AbstractVerificationSupport
{
	/**
	 * Service to send mails.
	 */
	@Autowired(required = false)
	private EmailService emailService;
	
	@Value("${webutils.email.verification.template:}")
	private String emailVerificationTemplateName;
	
	public EmailVerificationSupport()
	{
		super(VerificationType.EMAIL);
	}

	@Override
	public void sendCode(String emailId, String code) throws CodeDeliveryException
	{
		if(emailService == null)
		{
			throw new InvalidStateException("No email service is configured");
		}
		
		if(StringUtils.isBlank(emailVerificationTemplateName))
		{
			throw new InvalidStateException("No email-template is configured for sending verification code.");
		}
		
		emailService.sendEmail(emailVerificationTemplateName, CommonUtils.toMap("mailId", emailId, "otp", code));
	}
}
