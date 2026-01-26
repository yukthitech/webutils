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
package com.webutils.services.form.otp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.webutils.common.form.otp.OtpValidator;
import com.webutils.common.form.otp.OtpVerification;
import com.webutils.common.form.otp.VerificationType;
import com.webutils.services.auth.UserContext;
import com.webutils.services.common.ExecutionService;
import com.webutils.services.common.InvalidRequestException;
import com.webutils.services.user.UserService;
import com.yukthitech.utils.Encryptor;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.experimental.Accessors;

@Service
public class OtpService
{
	private static Logger logger = LogManager.getLogger(OtpService.class);
	
	private static Pattern TOKEN_PATTERN = Pattern.compile("otp\\:(\\w+)\\;(.+?);(\\w+);(\\d+)");

	private static String OTP_PREF_PREFIX = "$otpDetails-";

	@Data
	@Accessors(chain = true)
	public static class UserOtpDetails
	{
		private Date lastGeneratedTime;
		private int attempts;

		public boolean isLessThan(long durationMillis)
		{
			return lastGeneratedTime != null && System.currentTimeMillis() - lastGeneratedTime.getTime() < durationMillis;
		}

		public boolean isGreaterThan(long durationMillis)
		{
			return lastGeneratedTime != null && System.currentTimeMillis() - lastGeneratedTime.getTime() > durationMillis;
		}
	}
	
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

	@Autowired
	private UserService userService;
	
    @Autowired
    private ExecutionService executionService;

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

	@Value("${webutils.verification.otp.maxAttempts:3}")
	private int maxOtpAttempts;

	@Value("${webutils.verification.otp.minRetryDurationSec:30}")
	private long minRetryDurationSec;

	@Value("${webutils.verification.otp.maxAttemptsDurationHours:24}")
	private long maxAttemptsDurationHour;

	/**
	 * List of verification supports.
	 */
	private Map<VerificationType, IOtpSupport> verificationSupportes = new HashMap<>();
	
	@PostConstruct
	private void init()
	{
		if(encryptor == null)
		{
			logger.warn("As no encryptor is configured, skipping initialization of this service");
			return;
		}
		
		Map<String, IOtpSupport> supporters = applicationContext.getBeansOfType(IOtpSupport.class);
		
		if(supporters == null)
		{
			return;
		}
		
		for(IOtpSupport supporter : supporters.values())
		{
			verificationSupportes.put(supporter.getVerificationType(), supporter);
			logger.debug("Adding verification support for type {} using class: {}", supporter.getVerificationType(), supporter.getClass().getName());
		}
		
		OtpValidator.setValidatorFunction(this::verify);

		executionService.scheduleRepeatedTask("OtpService.cleanUpOtpDetails", this::cleanUpOtpDetails, 1, TimeUnit.DAYS);
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

		String otpPrefKey = OTP_PREF_PREFIX + type.name();
		UserOtpDetails userOtpDetails = (UserOtpDetails) userService.getUserPreference(UserContext.getCurrentUser().getId(), otpPrefKey);

		if(userOtpDetails == null)
		{
			userOtpDetails = new UserOtpDetails();
		}

		// check if last generated time is within min retry duration
		if(userOtpDetails.isLessThan(minRetryDurationSec * 1000))
		{
			long diffSec = minRetryDurationSec - (long) Math.floor((System.currentTimeMillis() - userOtpDetails.getLastGeneratedTime().getTime()) / 1000);
			throw new InvalidRequestException("Minimum retry duration not met. Please retry after {} seconds.", diffSec);
		}

		// if max attempts duration is reached, set the attempts to 0
		if(userOtpDetails.isGreaterThan(maxAttemptsDurationHour * 3600 * 1000))
		{
			userOtpDetails.setAttempts(0);
		}

		// check if max attempts are reached
		if(userOtpDetails.getAttempts() >= maxOtpAttempts)
		{
			long diffHours = maxAttemptsDurationHour - (long) Math.floor((System.currentTimeMillis() - userOtpDetails.getLastGeneratedTime().getTime()) / 1000 / 3600);
			throw new InvalidRequestException("Maximum attempts reached. Please try again after {} hours.", diffHours);
		}
		
		IOtpSupport support = verificationSupportes.get(type);
		
		if(support == null)
		{
			throw new InvalidArgumentException("Specified verification type is not supported: {}", type);
		}
		
		// TODO: Generate random code.
		String code = "4444";
		
		support.sendCode(value, code);
		
		String encodedString = String.format("otp:%s;%s;%s;%s", type, value, code, System.currentTimeMillis());
		String res = encryptor.encrypt(encodedString);

		// set updated user otp details
		userOtpDetails
			.setLastGeneratedTime(new Date())
			.setAttempts(userOtpDetails.getAttempts() + 1);
		userService.setUserPreference(otpPrefKey, userOtpDetails);

		return res;
	}
	
	private void verify(VerificationType verificationType, OtpVerification otp)
	{
		if(encryptor == null)
		{
			throw new InvalidStateException("No encryptor is configured, which is needed by this service");
		}

		String encodedString = null;
		
		try
		{
			encodedString = encryptor.decrypt(otp.getToken());
		}catch(Exception ex)
		{
			throw new InvalidRequestException("Invalid token specified");
		}
		
		Matcher matcher = TOKEN_PATTERN.matcher(encodedString);
		
		if(!matcher.matches())
		{
			throw new InvalidRequestException("Invalid token specified");
		}
		
		/*
		 * Fail the verification if type, value or generate code is not matching.
		 */
		if(!matcher.group(1).equals(verificationType.name()) ||
				!matcher.group(2).equals(otp.getValueToVerify()) ||
				(otp != null && !matcher.group(3).equals(otp.getValue()))
				)
		{
			throw new InvalidRequestException("Specified OTP code is not valid.");
		}
		
		long tokenTime = Long.parseLong(matcher.group(4));
		long curTime = System.currentTimeMillis();
		
		long diffSec = (curTime - tokenTime) / 1000;
		
		if(diffSec < 0 || diffSec > maxVerTokenTimeSec)
		{
			throw new InvalidRequestException("Token expired.");
		}
	}

	private void cleanUpOtpDetails()
	{
		userService.cleanUpOldPreferences(OTP_PREF_PREFIX + VerificationType.MOBILE.name(), new Date(System.currentTimeMillis() - maxAttemptsDurationHour * 3600 * 1000));
		userService.cleanUpOldPreferences(OTP_PREF_PREFIX + VerificationType.EMAIL.name(), new Date(System.currentTimeMillis() - maxAttemptsDurationHour * 3600 * 1000));
	}
}
