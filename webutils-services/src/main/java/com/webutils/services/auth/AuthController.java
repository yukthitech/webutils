package com.webutils.services.auth;

import java.io.IOException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webutils.common.IWebUtilsConstants;
import com.webutils.common.UserDetails;
import com.webutils.common.auth.ChangePasswordRequest;
import com.webutils.common.auth.LoginOtpRequest;
import com.webutils.common.auth.LoginRequest;
import com.webutils.common.auth.LoginResponse;
import com.webutils.common.auth.NoAuthentication;
import com.webutils.common.auth.ResetPasswordRequest;
import com.webutils.common.auth.UsernameRequest;
import com.webutils.common.form.otp.OtpPurpose;
import com.webutils.common.form.otp.SendOtpResponse;
import com.webutils.common.form.otp.VerificationType;
import com.webutils.common.response.BaseResponse;
import com.webutils.common.response.BasicReadResponse;
import com.webutils.services.common.InvalidRequestException;
import com.webutils.services.form.otp.OtpService;
import com.webutils.services.token.AuthTokenService;
import com.webutils.services.user.UserEntity;
import com.webutils.services.user.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

/**
 * Authentication controller — password login, OTP login, password recovery.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController
{
	private static final Logger logger = LogManager.getLogger(AuthController.class);

	@Autowired
	private AuthTokenService authTokenService;

	@Autowired
	private UserService userService;

	@Autowired
	private OtpService otpService;
	
	@Value("${app.webutils.userSpaceEnabled:false}")
	private boolean userSpaceEnabled;

	@Value("${webutils.verification.otp.loginMaxAttempts:3}")
	private int loginMaxAttempts;

	@Value("${webutils.verification.otp.lockoutDurationSec:3600}")
	private int lockoutDurationSec;

	@NoAuthentication
	@PostMapping("/login")
	public LoginResponse login(@RequestBody @Valid LoginRequest loginRequest, HttpServletResponse response)
	{
		String userSpace = requireUserSpace(loginRequest.getUserSpace());
		
		UserDetails userDetails = authTokenService.authenticate(
			loginRequest.getUsername(), 
			loginRequest.getPassword(),
			userSpace);
		
		return buildLoginResponse(userDetails, response);
	}

	@NoAuthentication
	@PostMapping("/otp/send-login")
	public BasicReadResponse<SendOtpResponse> sendLoginOtp(@RequestBody @Valid UsernameRequest request)
	{
		String userSpace = requireUserSpace(request.getUserSpace());
		SendOtpResponse otp = otpService.sendOtpForUsername(request.getUsername(), userSpace, OtpPurpose.LOGIN);
		return new BasicReadResponse<>(otp);
	}

	@NoAuthentication
	@PostMapping("/login-otp")
	public LoginResponse loginOtp(@RequestBody @Valid LoginOtpRequest request, HttpServletResponse response)
	{
		String userSpace = requireUserSpace(request.getUserSpace());
		UserEntity user = userService.requireUser(request.getUsername(), userSpace);
		userService.assertNotOtpBlocked(user);

		VerificationType type = userService.isEmailUsername(request.getUsername())
				? VerificationType.EMAIL
				: VerificationType.MOBILE;

		try
		{
			otpService.verifyOtp(type, request.getOtp(), OtpPurpose.LOGIN, user.getId());
		}
		catch(InvalidRequestException ex)
		{
			handleOtpLoginFailure(user);
			throw ex;
		}

		userService.clearOtpLoginFailures(user.getId());
		UserDetails userDetails = userService.getUserDetails(user);
		authTokenService.createSession(userDetails, userSpace);
		return buildLoginResponse(userDetails, response);
	}

	@NoAuthentication
	@PostMapping("/forgot-password")
	public BasicReadResponse<SendOtpResponse> forgotPassword(@RequestBody @Valid UsernameRequest request)
	{
		String userSpace = requireUserSpace(request.getUserSpace());
		SendOtpResponse otp = otpService.sendOtpForUsername(request.getUsername(), userSpace, OtpPurpose.RESET);
		return new BasicReadResponse<>(otp);
	}

	@NoAuthentication
	@PostMapping("/reset-password")
	public BaseResponse resetPassword(@RequestBody @Valid ResetPasswordRequest request)
	{
		String userSpace = requireUserSpace(request.getUserSpace());
		UserEntity user = userService.requireUser(request.getUsername(), userSpace);

		VerificationType type = userService.isEmailUsername(request.getUsername())
				? VerificationType.EMAIL
				: VerificationType.MOBILE;

		otpService.verifyOtp(type, request.getOtp(), OtpPurpose.RESET, user.getId());
		userService.updatePassword(user.getId(), request.getNewPassword());
		logger.info("Password reset completed for user id={}", user.getId());
		return new BaseResponse();
	}

	@PostMapping("/change-password")
	public BaseResponse changePassword(@RequestBody @Valid ChangePasswordRequest request)
	{
		UserDetails current = UserContext.getCurrentUser();
		UserEntity user = userService.requireUserById(current.getId());

		userService.verifyCurrentPassword(user, request.getCurrentPassword());
		userService.updatePassword(user.getId(), request.getNewPassword());
		logger.info("Password changed for user id={}", user.getId());
		return new BaseResponse();
	}

	/**
	 * Logout user
	 */
	@PostMapping("/logout")
	public BaseResponse logout(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		authTokenService.revokeToken();
		return new BaseResponse();
	}

	private void handleOtpLoginFailure(UserEntity user)
	{
		int failures = userService.getOtpLoginFailureCount(user.getId()) + 1;
		userService.setOtpLoginFailureCount(user.getId(), failures);

		if(failures >= loginMaxAttempts)
		{
			Date until = new Date(System.currentTimeMillis() + lockoutDurationSec * 1000L);
			userService.blockOtpLogin(user.getId(), until);
			logger.warn("OTP login blocked for user id={} until {}", user.getId(), until);
			throw new InvalidRequestException("Account blocked for OTP login until {}", until)
				.addParameter("errorType", "otpBlocked")
				.addParameter("otpBlockedUntil", until.getTime());
		}
	}

	private String requireUserSpace(String userSpace)
	{
		if(userSpaceEnabled)
		{
			if(StringUtils.isBlank(userSpace))
			{
				throw new InvalidRequestException("No user space specified");
			}
			return userSpace;
		}

		return userSpace == null ? "" : userSpace;
	}

	private LoginResponse buildLoginResponse(UserDetails userDetails, HttpServletResponse response)
	{
		Cookie authCookie = new Cookie(IWebUtilsConstants.SESSION_TOKEN_HEADER, userDetails.getAuthToken());
		authCookie.setHttpOnly(true);
		authCookie.setSecure(true);
		authCookie.setPath("/");
		response.addCookie(authCookie);

		return new LoginResponse(userDetails.getId(), userDetails.getAuthToken(), userDetails.getRoles());
	}
}
