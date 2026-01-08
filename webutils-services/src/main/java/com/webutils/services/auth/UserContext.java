package com.webutils.services.auth;

import com.webutils.common.UserDetails;

/**
 * Thread-local context to store current user details
 * 
 * Allows any controller or service to access current user information
 */
public class UserContext 
{
    private static final ThreadLocal<UserDetails> currentUser = new ThreadLocal<>();
    
    /**
     * Set current user details for this thread
     * 
     * @param userDetails the user details to store
     */
    public static void setCurrentUser(UserDetails userDetails) 
    {
        currentUser.set(userDetails);
    }
    
    /**
     * Get current user details for this thread
     * 
     * @return current user details, null if not set
     */
    public static UserDetails getCurrentUser() 
    {
        return currentUser.get();
    }
    
    /**
     * Get current user ID
     * 
     * @return current user ID, null if not set
     */
    public static Long getCurrentUserId() 
    {
        UserDetails user = currentUser.get();
        return user != null ? user.getId() : null;
    }
    
    /**
     * Clear current user details from this thread
     */
    public static void clear() 
    {
        currentUser.remove();
    }
}
