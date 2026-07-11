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

import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.webutils.common.UserDetails;
import com.webutils.common.form.otp.OtpPurpose;
import com.webutils.common.form.otp.OtpValidator;
import com.webutils.common.form.otp.OtpVerification;
import com.webutils.common.form.otp.SendOtpResponse;
import com.webutils.common.form.otp.VerificationType;
import com.webutils.services.auth.UserContext;
import com.webutils.services.common.ExecutionService;
import com.webutils.services.common.InvalidRequestException;
import com.webutils.services.common.UnauthorizedRequestException;
import com.webutils.services.form.token.TokenEntity;
import com.webutils.services.form.token.TokenManager;
import com.webutils.services.user.UserEntity;
import com.webutils.services.user.UserService;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.experimental.Accessors;

@Service
public class OtpService
{
	private static Logger logger = LogManager.getLogger(OtpService.class);

	/**
	 * Token value format (purpose is stored separately on {@code FORM_TOKEN.PURPOSE}):
	 * {@code otp:TYPE;target;code}
	 */
	private static Pattern TOKEN_PATTERN = Pattern.compile("otp\\:(\\w+)\\;(.+?);(\\w+)");

	private static String OTP_PREF_PREFIX = "$otpDetails-";

	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

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

	@Autowired
	private TokenManager tokenManager;

	@Autowired
	private UserService userService;
	
    @Autowired
    private ExecutionService executionService;

	/**
	 * Max time for which verification-token (otp) will be treated as valid. Default: 600 sec (10 min).
	 */
	@Value("${webutils.verification.otpExpiryTimeSec:600}")
	private int otpExpiryTimeSec;

	@Value("${webutils.verification.otp.maxAttempts:6}")
	private int maxOtpAttempts;

	@Value("${webutils.verification.otp.minRetryDurationSec:30}")
	private int minRetryDurationSec;

	@Value("${webutils.verification.otp.maxAttemptsDurationHours:24}")
	private int maxAttemptsDurationHour;

	@Value("${webutils.verification.otp.codeLength:6}")
	private int otpCodeLength;

	@Value("${app.devEnvironment:false}")
	private boolean isDevEnvironment;
	
	/**
	 * List of verification supports.
	 */
	private Map<VerificationType, IOtpSupport> verificationSupportes = new HashMap<>();
	
	@PostConstruct
	private void init()
	{
		if(tokenManager.isDisabled())
		{
			logger.info("OtpService is disabled as token manager is disabled");
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
	 * Generates and sends otp for the current authenticated user (form verification fields).
	 */
	public SendOtpResponse sendOtp(VerificationType type, String value) throws CodeDeliveryException
	{
		UserDetails userDetails = UserContext.getCurrentUser();
		return sendOtp(type, value, userDetails.getId(), userDetails, OtpPurpose.VERIFICATION.name());
	}

	/**
	 * Pre-auth OTP for a specific purpose (login or password reset). Channel is derived from
	 * username pattern ({@code @} → EMAIL, else MOBILE).
	 */
	public SendOtpResponse sendOtpForUsername(String username, String customSpace, OtpPurpose purpose)
			throws CodeDeliveryException
	{
		if(purpose == null || purpose == OtpPurpose.VERIFICATION)
		{
			logger.debug("Pre-auth OTP requires LOGIN or RESET purpose, but got {}", purpose);
			throw new UnauthorizedRequestException("OTP request is made in wrong context.");
		}

		UserEntity user = userService.requireUser(username, customSpace == null ? "" : customSpace);
		userService.assertNotOtpBlocked(user);

		VerificationType type = userService.isEmailUsername(username)
				? VerificationType.EMAIL
				: VerificationType.MOBILE;

		return sendOtp(type, username, user.getId(), null, purpose.name());
	}

	/**
	 * Generates and sends otp code for a specific user and purpose.
	 * Purpose is mandatory and stored on {@code FORM_TOKEN.PURPOSE} (not in the value string).
	 */
	private SendOtpResponse sendOtp(VerificationType type, String value, Long preferenceUserId,
			UserDetails deliveryUserDetails, String purpose) throws CodeDeliveryException
	{
		if(tokenManager.isDisabled())
		{
			throw new InvalidStateException("Otp service is disabled");
		}

		if(StringUtils.isBlank(purpose))
		{
			throw new InvalidArgumentException("OTP purpose is required and cannot be blank");
		}

		String otpPrefKey = OTP_PREF_PREFIX + "-" + type.name();
		UserOtpDetails userOtpDetails = (UserOtpDetails) userService.getUserPreference(preferenceUserId, otpPrefKey);

		if(userOtpDetails == null)
		{
			userOtpDetails = new UserOtpDetails();
		}

		// check if last generated time is within min retry duration
		if(userOtpDetails.isLessThan(minRetryDurationSec * 1000L))
		{
			long diffSec = minRetryDurationSec - (long) Math.floor((System.currentTimeMillis() - userOtpDetails.getLastGeneratedTime().getTime()) / 1000.0);
			throw new InvalidRequestException("Minimum retry duration not met. Please retry after {} seconds.", diffSec)
				.addParameter("errorType", "quickRetryAttempt")
				.addParameter("retryAfterSec", diffSec);
		}

		// if max attempts duration is reached, set the attempts to 0
		if(userOtpDetails.isGreaterThan(maxAttemptsDurationHour * 3600L * 1000L))
		{
			userOtpDetails.setAttempts(0);
		}

		// check if max attempts are reached
		if(userOtpDetails.getAttempts() >= maxOtpAttempts)
		{
			long diffHours = maxAttemptsDurationHour - (long) Math.floor((System.currentTimeMillis() - userOtpDetails.getLastGeneratedTime().getTime()) / 1000.0 / 3600.0);
			throw new InvalidRequestException("Maximum attempts reached. Please try again after {} hours.", diffHours)
				.addParameter("errorType", "maxAttemptsExpired")
				.addParameter("retryAfterHour", diffHours);
		}
		
		IOtpSupport support = verificationSupportes.get(type);
		
		if(support == null)
		{
			throw new InvalidArgumentException("Specified verification type is not supported: {}", type);
		}
		
		String code = generateOtpCode();
		
		if(isDevEnvironment)
		{
			logger.info("OTP delivery disabled in dev environment; skipping send for type={}, purpose={}, target={}",
					type, purpose, value);
		}
		else
		{
			support.sendCode(
				new OtpDetails()
					.setTarget(value)
					.setOtp(code)
					.setUserDetails(deliveryUserDetails)
				);
		}
		
		long curTime = System.currentTimeMillis();
		String tokenValue = String.format("otp:%s;%s;%s", type, value, code);
		String token = tokenManager.saveToken(tokenValue, otpExpiryTimeSec, purpose, preferenceUserId);

		// set updated user otp details
		userOtpDetails
			.setLastGeneratedTime(new Date())
			.setAttempts(userOtpDetails.getAttempts() + 1);
		userService.setUserPreference(preferenceUserId, otpPrefKey, userOtpDetails);

		return new SendOtpResponse()
				.setType(type)
				.setValue(value)
				.setToken(token)
				.setAttemptsRemaining(maxOtpAttempts - userOtpDetails.getAttempts())
				.setRetryAfterSec(minRetryDurationSec)
				.setExpiresOn(curTime + otpExpiryTimeSec * 1000L)
				;
	}

	/**
	 * Verifies OTP for the given type, purpose, and user. Purpose and userId are mandatory.
	 */
	public void verifyOtp(VerificationType verificationType, OtpVerification otp, OtpPurpose purpose, Long userId)
	{
		verify(verificationType, otp, purpose, userId);
	}

	private String generateOtpCode()
	{
		int length = Math.max(4, Math.min(otpCodeLength, 8));
		int bound = (int) Math.pow(10, length);
		int code = SECURE_RANDOM.nextInt(bound);
		return String.format("%0" + length + "d", code);
	}

	/**
	 * Form-validator callback — always expects {@link OtpPurpose#VERIFICATION} for the current user.
	 */
	private void verify(VerificationType verificationType, OtpVerification otp)
	{
		UserDetails current = UserContext.getCurrentUser();
		verify(verificationType, otp, OtpPurpose.VERIFICATION, current.getId());
	}
	
	private void verify(VerificationType verificationType, OtpVerification otp, OtpPurpose purpose, Long userId)
	{
		if(tokenManager.isDisabled())
		{
			throw new InvalidStateException("Otp service is disabled");
		}

		if(purpose == null)
		{
			logger.debug("OTP verification requires LOGIN or RESET purpose, but got null");
			throw new UnauthorizedRequestException("OTP verification is made in wrong context.");
		}

		if(userId == null)
		{
			throw new InvalidArgumentException("User id is required for OTP verification");
		}

		TokenEntity tokenEntity = tokenManager.fetchTokenEntity(otp.getToken(), purpose.name(), userId);

		if(tokenEntity == null)
		{
			throw new InvalidRequestException("Invalid or expired token specified");
		}
		
		Matcher matcher = TOKEN_PATTERN.matcher(tokenEntity.getValue());
		
		if(!matcher.matches())
		{
			throw new InvalidRequestException("Invalid token specified");
		}
		
		if(!matcher.group(1).equals(verificationType.name()) ||
				!matcher.group(2).equals(otp.getValueToVerify()) ||
				!matcher.group(3).equals(otp.getValue()))
		{
			throw new InvalidRequestException("Specified OTP code is not valid.");
		}

		// once token is validated, delete the otp token
		tokenManager.deleteToken(tokenEntity.getId());
	}

	private void cleanUpOtpDetails()
	{
		Date before = new Date(System.currentTimeMillis() - maxAttemptsDurationHour * 3600L * 1000L);
		userService.cleanUpOldPreferences(OTP_PREF_PREFIX + VerificationType.MOBILE.name(), before);
		userService.cleanUpOldPreferences(OTP_PREF_PREFIX + VerificationType.EMAIL.name(), before);
	}
}
