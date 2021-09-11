package com.yukthitech.webutils.security;

import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.persistence.utils.PasswordEncryptor;
import com.yukthitech.webutils.InvalidRequestParameterException;
import com.yukthitech.webutils.WebutilsConfiguration;
import com.yukthitech.webutils.common.UserDetails;
import com.yukthitech.webutils.repository.UserEntity;
import com.yukthitech.webutils.services.CurrentUserService;
import com.yukthitech.webutils.services.UserService;

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

		String dbPassword = userService.getPassword(userName, userSpace);

		// if user is not found or if password does not match
		if(dbPassword == null || !PasswordEncryptor.isSamePassword(dbPassword, password))
		{
			return null;
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
			throw new InvalidRequestParameterException("Specified current password is not valid.");
		}
		
		userService.updatePassword(currentUser.getUserSpace(), currentUser.getUserName(), newPassword);
	}
	
	@Override
	public String resetPassword(String userName, Map<String, String> attributes)
	{
		String userSpace = getUserSpace(userName, attributes);
		String randPassword = RandomStringUtils.randomAlphanumeric(8);
		
		if(!userService.updatePassword(userSpace, userName, randPassword))
		{
			throw new InvalidRequestParameterException("No user found with specified user-name: {}", userName);
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
		return "";
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
