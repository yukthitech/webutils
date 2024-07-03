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

package com.yukthitech.webutils.verification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.yukthitech.webutils.annotations.ActionName;
import com.yukthitech.webutils.annotations.NoAuthentication;
import com.yukthitech.webutils.common.verification.IVerificationController;
import com.yukthitech.webutils.common.verification.OtpVerificationRequest;
import com.yukthitech.webutils.common.verification.OtpVerificationResponse;
import com.yukthitech.webutils.common.verification.SendOtpResponse;
import com.yukthitech.webutils.common.verification.VerificationType;
import com.yukthitech.webutils.controllers.BaseController;

import jakarta.validation.Valid;

/**
 * Controller for managing verification.
 * @author akiran
 */
@RestController
@ActionName("verification")
@RequestMapping("/verification")
public class VerificationController extends BaseController implements IVerificationController
{
	/**
	 * Service to manage verifications.
	 */
	@Autowired
	private VerificationService verificationService;
	
	@NoAuthentication
	@Override
	@ActionName("sendOtp")
	@ResponseBody
	@RequestMapping(value = "/sendOtp/{type}/{value}", method = RequestMethod.POST)
	public SendOtpResponse sendOtp(@PathVariable("type") VerificationType type, @PathVariable("value") String value)
	{
		String token = verificationService.sendOtp(type, value);
		return new SendOtpResponse(token);
	}

	@NoAuthentication
	@Override
	@ActionName("verify")
	@ResponseBody
	@RequestMapping(value = "/verify", method = RequestMethod.POST)
	public OtpVerificationResponse verify(@Valid @RequestBody OtpVerificationRequest request)
	{
		String token = verificationService.validateOtp(request);
		return new OtpVerificationResponse(token);
	}
}
