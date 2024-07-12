package com.yukthitech.webutils.security;

import java.util.Map;

import com.yukthitech.webutils.common.UserDetails;
import com.yukthitech.webutils.user.UserEntity;

/**
 * Authentication service base contract.
 * @author akiran
 * @param <R> Roles used by application.
 */
public interface IAuthenticationService<R extends Enum<R>>
{
	/**
	 * Implementation of this method should validate if given credentials are valid or not. 
	 * 
	 * @param userName User name to be validated.
	 * @param password Password to be validated.
	 * @param attributes Extra attributes from client for custom login
	 * @return User details representing matching user.
	 */
	public UserDetails<R> authenticate(String userName, String password, Map<String, String> attributes);
	
	/**
	 * Used to convert user entity to user details. Currently this is used by bootstrap service.
	 * @param userEntity user entity to convert
	 * @return converted user details
	 */
	public UserDetails<R> toUserDetails(UserEntity userEntity);
	
	/**
	 * Changes the specified user-name password to new password.
	 * @param currentPassword Old password to validate before setting new password. 
	 * @param newPassword password to set.
	 */
	public void changePassword(String currentPassword, String newPassword);
	
	/**
	 * Implementation of this method should reset the password of current user. 
	 * 
	 * @param userName User whose password to reset.
	 * @param attributes Extra attributes from client for custom login
	 * @return newly generated password
	 */
	public String resetPassword(String userName, Map<String, String> attributes);
}
