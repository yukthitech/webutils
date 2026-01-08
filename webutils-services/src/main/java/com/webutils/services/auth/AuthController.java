package com.webutils.services.auth;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webutils.common.auth.LoginRequest;
import com.webutils.common.auth.LoginResponse;
import com.webutils.common.response.BaseResponse;
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

	@NoAuthentication
	@PostMapping("/login")
	public LoginResponse login(@RequestBody @Valid LoginRequest loginRequest)
	{
		/*
		UserDetails userDetails = userService.authenticate(
			loginRequest.getMailId(), 
			loginRequest.getPassword(),
			loginRequest.getClientType());

		return new LoginResponse(userDetails.getId(), userDetails.getAuthToken(), userDetails.getRoles());
		*/
		return null;
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
