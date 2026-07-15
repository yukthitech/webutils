package com.webutils.testapp.auth;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webutils.common.IWebUtilsConstants;
import com.webutils.common.UserDetails;
import com.webutils.common.auth.LoginResponse;
import com.webutils.common.auth.NoAuthentication;
import com.webutils.services.common.InvalidRequestException;
import com.webutils.services.token.AuthTokenService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

/**
 * Test-harness login that allows short passwords (seed user {@code test@test.com} / {@code test}).
 */
@RestController
@RequestMapping("/api/testapp/auth")
public class TestAppAuthController
{
	@Autowired
	private AuthTokenService authTokenService;

	@Value("${app.webutils.userSpaceEnabled:false}")
	private boolean userSpaceEnabled;

	@NoAuthentication
	@PostMapping("/login")
	public LoginResponse login(@RequestBody @Valid TestAppLoginRequest request, HttpServletResponse response)
	{
		String userSpace = requireUserSpace(request.getUserSpace());
		UserDetails userDetails = authTokenService.authenticate(
				request.getUsername(),
				request.getPassword(),
				userSpace);

		Cookie authCookie = new Cookie(IWebUtilsConstants.SESSION_TOKEN_HEADER, userDetails.getAuthToken());
		authCookie.setHttpOnly(true);
		authCookie.setSecure(false);
		authCookie.setPath("/");
		response.addCookie(authCookie);

		return new LoginResponse(userDetails.getId(), userDetails.getAuthToken(), userDetails.getRoles());
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
}
