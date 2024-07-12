package com.yukthitech.webutils.security;

import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.persistence.utils.PasswordEncryptor;
import com.yukthitech.webutils.InvalidRequestException;
import com.yukthitech.webutils.WebutilsConfiguration;
import com.yukthitech.webutils.common.UserDetails;
import com.yukthitech.webutils.services.CurrentUserService;
import com.yukthitech.webutils.user.UserEntity;
import com.yukthitech.webutils.user.UserPasswords;
import com.yukthitech.webutils.user.UserService;

/**
 * Default authentication service which simply checks user name and password combination.
 * @author akiran
 * @param <R> roles used by application
 */
public class DefaultAuthenticationService<R extends Enum<R>> implements IAuthenticationService<R>
{
	/**
	 * Used to fetch date format to be used.
	 */
	@Autowired
	protected WebutilsConfiguration webutilsConfiguration;

	/**
	 * User service for authentication.
	 */
	@Autowired
	private UserService userService;
	
	@Autowired
	private CurrentUserService currentUserService;

	@Override
	public UserDetails<R> authenticate(String userName, String password, Map<String, String> attributes)
	{
		String userSpace = getUserSpace(userName, attributes);
		UserPasswords dbPasswords = userService.getPassword(userName, userSpace);
		boolean resetPasswordUsed = false;
		boolean passwordMatched = false;

		// if user is not found or if password does not match
		if(dbPasswords != null)
		{
			if(PasswordEncryptor.isSamePassword(dbPasswords.getPassword(), password))
			{
				passwordMatched = true;
			}
			else if(dbPasswords.getResetPassword() != null && PasswordEncryptor.isSamePassword(dbPasswords.getResetPassword(), password))
			{
				passwordMatched = true;
				resetPasswordUsed = true;
			}
		}

		if(!passwordMatched)
		{
			return null;
		}
		
		// if auth is successful using reset password, then set reset password as main password
		if(resetPasswordUsed)
		{
			userService.updatePassword(dbPasswords.getId(), dbPasswords.getResetPassword());
		}
		
		UserEntity user = userService.getUser(userName, userSpace);
		return toUserDetails(user);
	}
	
	@Override
	public void changePassword(String currentPassword, String newPassword)
	{
		UserDetails<?> currentUser = currentUserService.getCurrentUserDetails();
		String dbPassword = userService.getPassword(currentUser.getUserId());

		// if user is not found or if password does not match
		// Note: the password change is being done for already authenticated user
		if(dbPassword == null || !PasswordEncryptor.isSamePassword(dbPassword, currentPassword))
		{
			throw new InvalidRequestException("Specified current password is not valid.");
		}
		
		userService.updatePassword(currentUser.getUserId(), newPassword);
	}
	
	@Override
	public String resetPassword(String userName, Map<String, String> attributes)
	{
		String userSpace = getUserSpace(userName, attributes);
		UserPasswords dbPasswords = userService.getPassword(userName, userSpace);
		
		if(dbPasswords == null)
		{
			throw new InvalidRequestException("No user found with specified user-name: {}", userName);
		}
		
		String randPassword = RandomStringUtils.random(10, 0, 0, true, true, '@', '#', '$', '%', '&', '*');
		
		if(!userService.updateResetPassword(dbPasswords.getId(), randPassword))
		{
			throw new InvalidRequestException("No user found with specified user-name: {}", userName);
		}
		
		return randPassword;
	}
	
	/**
	 * Can be overridden by child classes to provide application specific user space based
	 * on input user name and attributes provided during authentication.
	 * @param userName user name 
	 * @param attributes login app specific attributes
	 * @return user space under which user should be searched.
	 */
	protected String getUserSpace(String userName, Map<String, String> attributes)
	{
		return null;
	}
	
	/**
	 * Can be overridden by child classes to provide application specific user details which can contain roles
	 * etc.
	 * @param user user to be converted
	 * @return app specific user details
	 */
	@Override
	public UserDetails<R> toUserDetails(UserEntity user)
	{
		return new UserDetails<R>(user.getId(), user.getUserName(), user.getSpaceIdentity(), 
				user.getDisplayName(), webutilsConfiguration.getJsDateFormat());
	}
}
