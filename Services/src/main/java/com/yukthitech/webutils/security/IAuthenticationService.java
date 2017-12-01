package com.yukthitech.webutils.security;

import java.util.Map;

/**
 * Authentication service base contract.
 * @author akiran
 */
public interface IAuthenticationService
{
	/**
	 * Implementation of this method should validate if given credentials are valid or not. 
	 * 
	 * @param userName User name to be validated.
	 * @param password Password to be validated.
	 * @param attributes Extra attributes from client for custom login
	 * @return User details representing matching user.
	 */
	public UserDetails authenticate(String userName, String password, Map<String, String> attributes);
}
