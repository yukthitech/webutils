package com.webutils.services.common;

import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.webutils.common.UserDetails;
import com.webutils.common.auth.Authorization;
import com.webutils.common.auth.NoAuthentication;
import com.webutils.services.auth.UserContext;

@Service
public class SecurityService
{
	public void checkAuthorization(boolean isAuthRequired, Set<String> supportedRoles)
	{
		UserDetails userDetails = UserContext.getCurrentUser();
		
		if(userDetails == null)
		{
			if(!isAuthRequired)
			{
				return;
			}
			
			throw new UnauthenticatedRequestException("No user found on session");
		}
		
		if(CollectionUtils.isEmpty(supportedRoles))
		{
			return;
		}
		
        Set<String> userRoles = userDetails.getRoles();
        userRoles = (userRoles == null) ? Collections.emptySet() : userRoles;
        
        for(String role : supportedRoles)
        {
        	if(userRoles.contains(role))
        	{
        		return;
        	}
        }
        
        throw new UnauthorizedRequestException("User does not have required access");
		
		
	}
	
	public void checkAuthorization(Authorization authorization)
	{
        // if auth annotation is not present, invoke the method
        if(authorization == null)
        {
        	return;
        }
        
        checkAuthorization(true, Set.of(authorization.value()));
	}
	
	public void checkAuthorization(AnnotatedElement annotatedElement)
	{
		Authorization authorization = annotatedElement.getAnnotation(Authorization.class);
		
		checkAuthorization(
				annotatedElement.getAnnotation(NoAuthentication.class) == null, 
				authorization == null ? null : Set.of(authorization.value())
				);
	}

}
