package com.webutils.services.common;

import java.util.Set;

import com.webutils.common.FileInfo;
import com.webutils.common.IWebUtilsConstants;
import com.webutils.common.UserDetails;
import com.webutils.common.auth.StateCheckRequest;
import com.webutils.services.auth.UserContext;
import com.webutils.services.user.UserEntity;

/**
 * Basic integration support to be provided by applications.
 */
public interface IWebutilsService
{
	public default UserDetails getUserDetails(UserEntity user)
	{
        String customSpace = user.getCustomSpace();
        Set<String> roles = null;

        if(IWebUtilsConstants.TEST_USER_SPACE.equals(customSpace))
        {
        	roles = Set.of(IWebUtilsConstants.ROLE_TEST);
        }

        UserDetails userDetails = new UserDetails(user.getId(), user.getName(),
            user.getEmail(), customSpace, null, roles
            );

        return userDetails;
	}

	/**
	 * Apps override to reject operations that are illegal in the current user/domain state.
	 * Default is a no-op.
	 */
	public default void checkOperationState(StateCheckRequest request)
	{
	}
	
	public default boolean isFileAccessible(FileInfo fileInfo)
	{
		UserDetails userDetails = UserContext.getCurrentUser();
		return (userDetails.getId() == fileInfo.getOwnerId());
	}
}
