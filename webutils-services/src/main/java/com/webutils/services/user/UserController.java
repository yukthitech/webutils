package com.webutils.services.user;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webutils.common.UserDetails;
import com.webutils.common.response.BaseResponse;
import com.webutils.common.response.BasicReadResponse;
import com.webutils.common.user.UserPreference;
import com.webutils.services.auth.UserContext;
import com.webutils.services.common.InvalidRequestException;

import jakarta.validation.Valid;

/**
 * Sample controller demonstrating how to access current user information
 * 
 * Shows how any controller can easily get current user details using the session-based authorization
 */
@RestController
@RequestMapping("/api/user")
public class UserController 
{
    private static Logger logger = LogManager.getLogger(UserController.class);
    
    private static final Pattern KEY_PATTERN = Pattern.compile("\\w+");
    
    @Autowired
    private UserService userService;

    /**
     * Get current user profile
     * 
     * @return current user details
     */
    @GetMapping("/profile")
    public BasicReadResponse<UserDetails> getCurrentUserProfile() 
    {
        logger.debug("Getting current user profile");
        
        UserDetails currentUser = UserContext.getCurrentUser();
        return new BasicReadResponse<UserDetails>(currentUser);
    }

    private void validateKey(String key)
    {
        Matcher matcher = KEY_PATTERN.matcher(key);
        if(!matcher.matches())
        {
            throw new InvalidRequestException("Invalid key: " + key);
        }
    }

    @PostMapping("/preference")
    public BaseResponse setPreference(@RequestBody @Valid UserPreference preference)
    {
        logger.debug("Setting preference [Key: {}, Value: {}]", preference.getKey(), preference.getValue());

        validateKey(preference.getKey());
        
        userService.setUserPreference(preference.getKey(), preference.getValue());
        return new BaseResponse();
    }

    @GetMapping("/preference/{key}")
    public BasicReadResponse<Object> getPreference(@PathVariable("key") String key)
    {
        logger.debug("Getting preference [Key: {}]", key);
        
        validateKey(key);

        Object value = userService.getUserPreference(UserContext.getCurrentUser().getId(), key);
        return new BasicReadResponse<>(value);
    }
}
