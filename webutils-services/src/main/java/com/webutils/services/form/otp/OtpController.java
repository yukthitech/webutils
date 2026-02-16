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

package com.webutils.services.form.otp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webutils.common.form.model.FieldDef;
import com.webutils.common.form.model.FieldType;
import com.webutils.common.form.otp.SendOtpResponse;
import com.webutils.common.form.otp.VerificationType;
import com.webutils.common.response.BasicReadResponse;
import com.webutils.services.common.InvalidRequestException;
import com.webutils.services.common.SecurityService;
import com.webutils.services.form.model.ModelService;
/**
 * Controller for fetching LOV values.
 * @author akiran
 */
@RestController
@RequestMapping("/api/otp")
public class OtpController
{
	@Autowired
	private OtpService otpService;

	@Autowired
	private SecurityService securityService;

	@Autowired
	private ModelService modelService;
	
	@PostMapping("/send/{fieldId}/{value}")
	public BasicReadResponse<SendOtpResponse> sendOtp(
		@PathVariable("fieldId") String fieldId, 
		@PathVariable("value") String value) {
		FieldDef fieldDef = modelService.getFieldDef(fieldId);

		if(fieldDef == null) {
			throw new InvalidRequestException("Field not found: {}", fieldId);
		}

		if(fieldDef.getFieldType() != FieldType.VERIFICATION) {
			throw new InvalidRequestException("Field is not an OTP field: {}", fieldId);
		}

		securityService.checkAuthorization(fieldDef.getField());
		
		VerificationType verificationType = fieldDef.getVerificationType();
		
		SendOtpResponse otp = otpService.sendOtp(verificationType, value);
		return new BasicReadResponse<>(otp);
	}
}
