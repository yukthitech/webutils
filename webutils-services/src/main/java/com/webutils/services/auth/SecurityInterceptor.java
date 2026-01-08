package com.webutils.services.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.webutils.common.IWebUtilsConstants;
import com.webutils.common.UserDetails;
import com.webutils.services.common.Authorization;
import com.webutils.services.common.NoAuthentication;
import com.webutils.services.common.UnauthenticatedRequestException;
import com.webutils.services.common.UnauthorizedRequestException;
import com.webutils.services.token.AuthTokenService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityInterceptor implements HandlerInterceptor
{
	private static Logger logger = LogManager.getLogger(SecurityInterceptor.class);
	
    @Autowired
    private AuthTokenService authTokenService;
    
    @Value("${app.login.uri}")
    private String loginUri;
    
	private String getSessionToken(HttpServletRequest request)
    {
        // Check if valid header token is present
        String sessionToken = request.getHeader(IWebUtilsConstants.SESSION_TOKEN_HEADER);

        if(sessionToken == null)
        {
            throw new UnauthenticatedRequestException("Invalid/no session token");
        }

        if(sessionToken.startsWith(IWebUtilsConstants.SESSION_BEARER_PREFIX))
        {
            sessionToken = sessionToken.substring(IWebUtilsConstants.SESSION_BEARER_PREFIX.length());
        }
        else
        {
            throw new UnauthenticatedRequestException("Invalid/no session token");
        }

        return sessionToken;
    }
	
	private void checkAuthorization(HandlerMethod handlerMethod, UserDetails userDetails)
	{
        Authorization authorization = handlerMethod.getMethodAnnotation(Authorization.class);
        
        // if auth annotation is not present, invoke the method
        if(authorization == null)
        {
        	return;
        }
        
        // if annotation is present, invoked if at least any one role is present
        
        String userRole = userDetails.getRole();
        
        for(String role : authorization.value())
        {
        	if(role.equals(userRole))
        	{
        		return;
        	}
        }
        
        throw new UnauthorizedRequestException("User is not having required access");
	}

	/**
	 * Spring prehandle method, which is used to check authorization.
	 * @param request Request
	 * @param response Response
	 * @param handler Handler method
	 * @return true, if authorized
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
	{
    	logger.trace("Intercepting request: {}", request.getRequestURI());
    	
    	// Check if this URI should be excluded from authentication
    	/*
    	if(shouldNotIntercept(request))
    	{
    		return true;
    	}
    	*/
    	
    	// for non-api resources dont check for tokens
    	if(!request.getRequestURI().startsWith("/api"))
    	{
    		return true;
    	}

		HandlerMethod handlerMethod = (HandlerMethod) handler;
		
		NoAuthentication noAuth = handlerMethod.getMethodAnnotation(NoAuthentication.class);
		
		if(noAuth != null)
		{
			return true;
		}
		
		try 
        {
            // Clear any existing user context
            UserContext.clear();
            
            // Validate session and get user details
            String sessionToken = getSessionToken(request);
            UserDetails userDetails = authTokenService.getUserDetails(sessionToken);
            logger.trace("Got the token details using SERVER token: {}", userDetails);
            
            // Set current user context
            UserContext.setCurrentUser(userDetails);
            
            // Continue with the request
            checkAuthorization(handlerMethod, userDetails);
            return true;
        } catch (UnauthenticatedRequestException e) 
        {
        	logger.info("Rejecting resource because of session unavailability: {}. Error: {}", request.getRequestURI(), e.getMessage());
        	logger.debug("Sending {} status response in json format", HttpServletResponse.SC_UNAUTHORIZED);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"message\":\"Unauthenticated access.\"}");
            return false;
        } catch (UnauthorizedRequestException e) 
        {
        	logger.info("Rejecting resource because of unauthorized access: {}. Error: {}", request.getRequestURI(), e.getMessage());
        	logger.debug("Sending {} status response in json format", HttpServletResponse.SC_FORBIDDEN);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"message\":\"Unauthorized access.\"}");
            return false;
        } catch(Exception ex)
        {
        	logger.info("An error occurred while intercepting request", ex);
        	
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"message\":\"Unexpected error occurred.\"}");
            return false;
        }
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception
	{
        // Always clear user context after request processing
        UserContext.clear();
	}
	
	/**
	 * Check if the request should not be intercepted (similar to shouldNotFilter in filter)
	 */
	/*
	private boolean shouldNotIntercept(HttpServletRequest request)
	{
		if(uriPatterns == null)
		{
			return false;
		}
		
		String path = request.getRequestURI();
		
		// Skip session validation for these paths
		for(String uri : this.uriPatterns)
		{
			// for "/" dont use as prefix. Check it with equals
			if("/".equals(uri))
			{
				if("/".equals(path))
				{
					return true;
				}
				
				continue;
			}
			
			if(path.startsWith(uri))
			{
				return true;
			}
		}

		return false;
	}
	*/

}
