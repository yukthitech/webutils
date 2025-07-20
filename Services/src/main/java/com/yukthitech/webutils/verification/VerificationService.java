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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.yukthitech.utils.Encryptor;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.webutils.InvalidRequestException;
import com.yukthitech.webutils.common.IWebUtilsCommonConstants;
import com.yukthitech.webutils.common.verification.OtpVerificationRequest;
import com.yukthitech.webutils.common.verification.VerificationType;
import com.yukthitech.webutils.common.verification.VerificationValidator;

import jakarta.annotation.PostConstruct;

@Service
public class VerificationService
{
	private static Logger logger = LogManager.getLogger(VerificationService.class);
	
	private static Pattern TOKEN_PATTERN = Pattern.compile("otp\\:(\\w+)\\;(.+?);(\\w+);(\\d+)");
	
	private static Pattern VERIFICATION_PATTERN = Pattern.compile("verified\\:(\\w+)\\;(.+?);(\\w+);(\\d+)");
	
	/**
	 * Context to fetch services supporting verification.
	 */
	@Autowired
	private ApplicationContext applicationContext;
	
	/**
	 * Used to encrypted message string.
	 */
	@Autowired(required = false)
	private Encryptor encryptor;
	
	/**
	 * Max otp token time for which it will be treated as valid. Default: 180 sec (3 min).
	 */
	@Value("${webutils.verification.otpTokenTimeSec:180}")
	private long maxOtpTokenTimeSec;
	
	/**
	 * Max verification token time for which it will be treated as valid. Default: 600 sec (10 min).
	 */
	@Value("${webutils.verification.verificationTokenTimeSec:600}")
	private long maxVerTokenTimeSec;

	/**
	 * List of verification supports.
	 */
	private Map<VerificationType, IVerificationSupport> verificationSupportes = new HashMap<>();
	
	@PostConstruct
	private void init()
	{
		if(encryptor == null)
		{
			logger.warn("As no encryptor is configured, skipping initialization of this service");
			return;
		}
		
		Map<String, IVerificationSupport> supporters = applicationContext.getBeansOfType(IVerificationSupport.class);
		
		if(supporters == null)
		{
			return;
		}
		
		for(IVerificationSupport supporter : supporters.values())
		{
			verificationSupportes.put(supporter.getVerificationType(), supporter);
			logger.debug("Adding verification support for type {} using class: {}", supporter.getVerificationType(), supporter.getClass().getName());
		}
		
		VerificationValidator.setValidatorFunction((verificationType, valueWithToken) -> 
		{
			try
			{
				validateVerification(verificationType, valueWithToken.getValue(), valueWithToken.getToken());
				return true;
			}catch(InvalidRequestException ex)
			{
				return false;
			}
		});
	}

	/**
	 * Generates and sends otp code.
	 * @param type Type of verification to be done. Eg: phone, email, etc.
	 * @param value Value to be verified. Eg: phone number, email id, etc.
	 * @return
	 */
	public String sendOtp(VerificationType type, String value) throws CodeDeliveryException
	{
		if(encryptor == null)
		{
			throw new InvalidStateException("No encryptor is configured, which is needed by this service");
		}
		
		IVerificationSupport support = verificationSupportes.get(type);
		
		if(support == null)
		{
			throw new InvalidArgumentException("Specified verification type is not supported: {}", type);
		}
		
		// TODO: Generate random code.
		String code = "4444";
		
		support.sendCode(value, code);
		
		String encodedString = String.format("otp:%s;%s;%s;%s", type, value, code, System.currentTimeMillis());
		return encryptor.encrypt(encodedString);
	}
	
	private void verify(String token, VerificationType type, String value, String otp, Pattern pattern, long maxTime)
	{
		if(encryptor == null)
		{
			throw new InvalidStateException("No encryptor is configured, which is needed by this service");
		}

		String encodedString = null;
		
		try
		{
			encodedString = encryptor.decrypt(token);
		}catch(Exception ex)
		{
			throw new InvalidRequestException("Invalid token specified");
		}
		
		Matcher matcher = pattern.matcher(encodedString);
		
		if(!matcher.matches())
		{
			throw new InvalidRequestException("Invalid token specified");
		}
		
		/*
		 * Fail the verification if type, value or generate code is not matching.
		 */
		if(!matcher.group(1).equals(type.name()) ||
				!matcher.group(2).equals(value) ||
				(otp != null && !matcher.group(3).equals(otp))
				)
		{
			throw new InvalidRequestException("Specified OTP code is not valid.");
		}
		
		long tokenTime = Long.parseLong(matcher.group(4));
		long curTime = System.currentTimeMillis();
		
		long diffSec = (curTime - tokenTime) / 1000;
		
		if(diffSec < 0 || diffSec > maxTime)
		{
			throw new InvalidRequestException(IWebUtilsCommonConstants.RESPONSE_CODE_EXPIRED_VALUE, "Token expired.");
		}
	}
	
	/**
	 * Verifies if specified verification request is valid.
	 * @param request request to be validated.
	 * @return token for successful verification.
	 */
	public String validateOtp(OtpVerificationRequest request)
	{
		verify(request.getToken(), request.getType(), request.getValue(), 
				request.getOtp(), TOKEN_PATTERN, maxOtpTokenTimeSec);

		String verEncodedString = String.format("verified:%s;%s;%s;%s", request.getType(), request.getValue(), request.getOtp(), System.currentTimeMillis());
		return encryptor.encrypt(verEncodedString);
	}
	
	private void validateVerification(VerificationType type, String value, String verificationToken)
	{
		verify(verificationToken, type, value, 
				null, VERIFICATION_PATTERN, maxVerTokenTimeSec);
	}
}
