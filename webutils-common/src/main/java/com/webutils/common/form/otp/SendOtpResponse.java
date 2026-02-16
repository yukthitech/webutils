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

package com.webutils.common.form.otp;

import com.webutils.common.response.BaseResponse;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Response for sending otp request.
 * @author akiran
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SendOtpResponse extends BaseResponse
{
	/**
	 * Type of verification performed.
	 */
	private VerificationType type;
	
	/**
	 * Valued being verified.
	 */
	private String value;
	
	/**
	 * Time in millis when the current token will expire.
	 */
	private long expiresOn;
	
	/**
	 * Token generated during sending OTP.
	 */
	private String token;
	
	/**
	 * Time after which otp generation can be retried.
	 */
	private int retryAfterSec;
	
	/**
	 * Number of times this user can retry sending otp.
	 */
	private int attemptsRemaining;
}
