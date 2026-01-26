package com.webutils.services.user;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.webutils.common.UserDetails;
import com.webutils.services.auth.UserContext;
import com.webutils.services.common.IWebutilsService;
import com.webutils.services.common.InvalidRequestException;
import com.yukthitech.persistence.utils.PasswordEncryptor;

@Service
public class UserService
{
    private static final Logger logger = LogManager.getLogger(UserService.class);

    @Value("${session.timeout.seconds:1800}")
    private int sessionTimeoutSec;

    @Value("${session.renewal.seconds:300}")
    private int sessionRenewalSec;

	@Autowired
	private IUserRepository userRepository;

    @Autowired
    private IUserPreferenceRepository userPreferenceRepository;
    
    @Autowired
    private IWebutilsService webutilsService;

    public long createUser(UserEntity user)
    {
        userRepository.save(user);
        return user.getId();
    }
    
    public UserDetails validate(String email, String password, String customSpace)
    {
        logger.debug("Authenticating user [Email: {}, Custom Space: {}]", email, customSpace);
        
        UserEntity user = userRepository.fetchUserByEmail(email, customSpace);

        if(user == null)
        {
            logger.debug("Authentication failed for user. No user exists with email: {}", email);
            throw new InvalidRequestException("Authentication failed for user: {}", email);
        }

        if(!PasswordEncryptor.isSamePassword(user.getPassword(), password))
        {
            logger.debug("Authentication failed for user. Invalid password for user: {}", email);
            throw new InvalidRequestException("Authentication failed for user: {}", email);
        }
        
        return getUserDetails(user);
    }

    public UserDetails getUserDetails(UserEntity user)
    {
        logger.debug("Getting user details [User: {}]", user.getId());
        return webutilsService.getUserDetails(user);
    }

    public Object getUserPreference(Long userId, String key)
    {
        logger.debug("Getting user preference [User: {}, Key: {}]", userId, key);
        
        UserPreferenceEntity userPreference = userPreferenceRepository.fetchByUserIdAndKey(userId, key);
        return userPreference == null ? null : userPreference.getValue();
    }

    /**
     * Sets the user preference for the current user.
     * 
     * Note: Preferences key coming from client is always alpha-numeric. And internal keys will start with $.
     * This is made so that internal keys are not exposed to client.
     * 
     * @param key
     * @param value
     */
    public void setUserPreference(String key, Object value)
    {
        logger.debug("Setting user preference [Key: {}, Value: {}]", key, value);
        
        UserDetails userDetails = UserContext.getCurrentUser();
        Long userId = userDetails.getId();

        UserPreferenceEntity userPreference = userPreferenceRepository.fetchByUserIdAndKey(userId, key);

        if(userPreference == null)
        {
            userPreference = new UserPreferenceEntity();
            userPreference.setUser(new UserEntity(userId));
        }

        userPreference.setKey(key);
        userPreference.setValue(value);

        if(userPreference.getId() == null)
        {
            userPreferenceRepository.save(userPreference);
        }
        else
        {
            userPreference.setLastUpdatedTime(new Date());
            userPreferenceRepository.update(userPreference);
        }

    }

    public void cleanUpOldPreferences(String key, Date beforeDate)
    {
        logger.debug("Cleaning up old preferences [Key: {}, Before Date: {}]", key, beforeDate);

        long deletedCount = userPreferenceRepository.deletePreferences(key, beforeDate);
        logger.debug("Deleted {} old preferences [Key: {}]", deletedCount, key);
    }
}
