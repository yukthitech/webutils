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
import com.webutils.services.common.UnauthenticatedRequestException;
import com.yukthitech.persistence.utils.PasswordEncryptor;

@Service
public class UserService
{
    private static final Logger logger = LogManager.getLogger(UserService.class);

    private static final String OTP_LOGIN_FAILURES_PREF = "$otpLoginFailures";

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
    
    public UserDetails validate(String username, String password, String customSpace)
    {
        logger.debug("Authenticating user [Username: {}, Custom Space: {}]", username, customSpace);
        
        UserEntity user = findByUsername(username, customSpace);

        if(user == null)
        {
            logger.debug("Authentication failed for user. No user exists with username: {}", username);
            throw new UnauthenticatedRequestException("Authentication failed for user: " + username);
        }

        if(!PasswordEncryptor.isSamePassword(user.getPassword(), password))
        {
            logger.debug("Authentication failed for user. Invalid password for user: {}", username);
            throw new UnauthenticatedRequestException("Authentication failed for user: " + username);
        }
        
        return getUserDetails(user);
    }

    /**
     * Resolves a user by email or mobile based on username pattern (email if contains '@', else mobile).
     */
    public UserEntity findByUsername(String username, String customSpace)
    {
        if(username != null && username.contains("@"))
        {
            return userRepository.fetchUserByEmail(username, customSpace);
        }
        return userRepository.fetchUserByMobile(username, customSpace);
    }

    /**
     * Resolves channel type from username pattern.
     */
    public boolean isEmailUsername(String username)
    {
        return username != null && username.contains("@");
    }

    public UserDetails getUserDetails(UserEntity user)
    {
        logger.debug("Getting user details [User: {}]", user.getId());
        return webutilsService.getUserDetails(user);
    }

    public UserEntity findById(Long userId)
    {
        return userRepository.findById(userId);
    }

    public UserEntity requireUserById(Long userId)
    {
        UserEntity user = userRepository.findById(userId);

        if(user == null)
        {
            throw new InvalidRequestException("User not found");
        }

        return user;
    }

    public UserEntity requireUser(String username, String customSpace)
    {
        UserEntity user = findByUsername(username, customSpace);

        if(user == null)
        {
            throw new InvalidRequestException("User not found");
        }

        return user;
    }

    public void assertNotOtpBlocked(UserEntity user)
    {
        if(user.getOtpBlockedUntil() != null && user.getOtpBlockedUntil().after(new Date()))
        {
            throw new InvalidRequestException("OTP login is blocked until {}", user.getOtpBlockedUntil())
                .addParameter("errorType", "otpBlocked")
                .addParameter("otpBlockedUntil", user.getOtpBlockedUntil().getTime());
        }
    }

    public int getOtpLoginFailureCount(Long userId)
    {
        Object value = getUserPreference(userId, OTP_LOGIN_FAILURES_PREF);
        return value instanceof Number ? ((Number) value).intValue() : 0;
    }

    public void setOtpLoginFailureCount(Long userId, int count)
    {
        setUserPreference(userId, OTP_LOGIN_FAILURES_PREF, count);
    }

    public void clearOtpLoginFailures(Long userId)
    {
        setUserPreference(userId, OTP_LOGIN_FAILURES_PREF, 0);
        userRepository.updateOtpBlockedUntil(null, userId);
    }

    public void blockOtpLogin(Long userId, Date until)
    {
        userRepository.updateOtpBlockedUntil(until, userId);
    }

    public void updatePassword(Long userId, String plainPassword)
    {
        UserEntity user = userRepository.findById(userId);

        if(user == null)
        {
            throw new InvalidRequestException("User not found");
        }

        // PasswordEncryptionConverter encrypts on entity update
        user.setPassword(plainPassword);
        user.setUpdatedOn(new Date());
        userRepository.update(user);
    }

    public void verifyCurrentPassword(UserEntity user, String currentPassword)
    {
        if(!PasswordEncryptor.isSamePassword(user.getPassword(), currentPassword))
        {
            throw new InvalidRequestException("Current password is incorrect");
        }
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
        UserDetails userDetails = UserContext.getCurrentUser();
        setUserPreference(userDetails.getId(), key, value);
    }

    public void setUserPreference(Long userId, String key, Object value)
    {
        logger.debug("Setting user preference [User: {}, Key: {}, Value: {}]", userId, key, value);

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
