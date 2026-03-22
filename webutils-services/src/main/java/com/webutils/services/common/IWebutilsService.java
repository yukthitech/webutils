package com.webutils.services.common;

import com.webutils.common.FileInfo;
import com.webutils.common.UserDetails;
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
        UserDetails userDetails = new UserDetails(user.getId(), user.getName(),
            user.getEmail(), customSpace, null, null
            );

        return userDetails;
	}
	
	public default boolean isFileAccessible(FileInfo fileInfo)
	{
		UserDetails userDetails = UserContext.getCurrentUser();
		return (userDetails.getId() == fileInfo.getOwnerId());
	}
}
