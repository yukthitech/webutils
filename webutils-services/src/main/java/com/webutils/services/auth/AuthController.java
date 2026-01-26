package com.webutils.services.auth;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webutils.common.UserDetails;
import com.webutils.common.auth.LoginRequest;
import com.webutils.common.auth.LoginResponse;
import com.webutils.common.response.BaseResponse;
import com.webutils.services.common.InvalidRequestException;
import com.webutils.services.common.NoAuthentication;
import com.webutils.services.token.AuthTokenService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

/**
 * Authentication controller for OAuth 2.0 and JWT authentication
 * 
 * Handles login, logout, and token management for the Acharya application
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController
{
	@Autowired
	private AuthTokenService authTokenService;
	
	@Value("${app.webutils.userSpaceEnabled:false}")
	private boolean userSpaceEnabled;

	@NoAuthentication
	@PostMapping("/login")
	public LoginResponse login(@RequestBody @Valid LoginRequest loginRequest)
	{
		if(userSpaceEnabled)
		{
			if(StringUtils.isBlank(loginRequest.getUserSpace()))
			{
				throw new InvalidRequestException("No user space specified");
			}
		}
		
		UserDetails userDetails = authTokenService.authenticate(
			loginRequest.getMailId(), 
			loginRequest.getPassword(),
			loginRequest.getUserSpace());

		return new LoginResponse(userDetails.getId(), userDetails.getAuthToken(), userDetails.getRoles());
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
}
