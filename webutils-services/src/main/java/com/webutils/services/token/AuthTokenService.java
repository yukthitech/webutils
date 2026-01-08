package com.webutils.services.token;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.webutils.common.UserDetails;
import com.webutils.services.auth.UserContext;
import com.webutils.services.common.ExecutionService;
import com.webutils.services.common.LruMap;
import com.webutils.services.common.UnauthenticatedRequestException;
import com.webutils.services.user.UserEntity;
import com.webutils.services.user.UserService;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.experimental.Accessors;

@Service
public class AuthTokenService 
{
    private static final Logger logger = LogManager.getLogger(AuthTokenService.class);
    
	@Data
	@Accessors(chain = true)
	public static class TokenDetails
	{
		private String authToken;

		private Date authTokenExpiresAt;

		private String role;
		
		private UserDetails userDetails;
		
		private Date lastUpdatedOn;
	}
	
    @Autowired
    private IAuthTokenRepository authTokenRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ExecutionService executionService;

    @Value("${session.timeout.seconds:1800}") // 30 minutes
    private long sessionTimeoutSec;

    @Value("${session.renew.interval.seconds:300}") // 5 minutes
    private long renewIntervalSec;

    @Value("${session.cleanup.interval.seconds:3600}") // 1 hour
    private long cleanupIntervalSec;

    private LruMap<String, TokenDetails> tokenCache = new LruMap<>(1000);

    @PostConstruct
    private void init()
    {
        executionService.scheduleRepeatedTask("AuthTokenService.cleanupExpiredTokens", 
        		this::cleanupExpiredTokens, cleanupIntervalSec, TimeUnit.SECONDS);
    }
    
    public UserDetails authenticate(String email, String password, String role)
    {
        logger.debug("Authenticating user [Email: {}, Role: {}]", email, role);
        
        UserDetails userDetails = userService.validate(email, password, role);
        
        generateToken(userDetails, role);
        
        return userDetails;
    }
    
    private String generateToken(UserDetails userDetails, String role)
    {
        logger.debug("Generating token [User: {}, Role: {}]", userDetails.getId(), role);
        
        String token = UUID.randomUUID().toString();
        long timeoutSec = sessionTimeoutSec;

        Date expiresAt = new Date(System.currentTimeMillis() + timeoutSec * 1000);
        Date now = new Date();

        AuthTokenEntity authToken = new AuthTokenEntity();
        authToken.setToken(token);
        authToken.setExpiresAt(expiresAt);
        authToken.setUser(new UserEntity(userDetails.getId()));
        authToken.setCreatedOn(now);
        authToken.setLastUpdatedOn(now);
        authToken.setRole(role);
        authTokenRepository.save(authToken);
        
        userDetails.setAuthToken(token);

        // set the token in the user details
        TokenDetails tokenDetails = new TokenDetails()
        		.setAuthToken(token)
        		.setAuthTokenExpiresAt(expiresAt)
        		.setRole(role)
        		.setUserDetails(userDetails)
        		.setLastUpdatedOn(now);
        
        tokenCache.put(token, tokenDetails);
        return token;
    }

    public UserDetails getUserDetails(String token)
    {
        logger.debug("Getting user details [Token: {}]", token);
        
    	TokenDetails cachedTokenDetails = tokenCache.get(token);

        if(cachedTokenDetails != null)
        {
            // check if the token is expired
            //  if expired, fetch from db to considered renewed expiry
            if(cachedTokenDetails.getAuthTokenExpiresAt().after(new Date()))
            {
                checkAndRenewToken(cachedTokenDetails);
                return cachedTokenDetails.userDetails;
            }
        }

        AuthTokenEntity authToken = authTokenRepository.fetchByToken(token);

        if(authToken == null)
        {
            throw new UnauthenticatedRequestException("Invalid token specified");
        }

        if(authToken.getExpiresAt().before(new Date()))
        {
            throw new UnauthenticatedRequestException("Token has expired");
        }

        UserDetails userDetails = userService.getUserDetails(authToken.getUser());
        
        TokenDetails tokenDetails = new TokenDetails()
        		.setAuthToken(token)
        		.setAuthTokenExpiresAt(authToken.getExpiresAt())
        		.setRole(authToken.getRole())
        		.setUserDetails(userDetails)
                .setLastUpdatedOn(authToken.getLastUpdatedOn());
        
        tokenCache.put(token, tokenDetails);
        return userDetails;
    }

    private void checkAndRenewToken(TokenDetails tokenDetails)
    {
        logger.debug("Checking and renewing token [Token: {}]", tokenDetails.getAuthToken());
        
        String token = tokenDetails.getAuthToken();
        long currentTime = System.currentTimeMillis();

        long diff = (currentTime - tokenDetails.getLastUpdatedOn().getTime()) / 1000;

        if(diff < renewIntervalSec)
        {
            return;
        }

        // cross check the expires at from db to avoid race condition
        AuthTokenEntity authTokenFromDb = authTokenRepository.fetchByToken(token);

        if(authTokenFromDb == null || authTokenFromDb.getExpiresAt().before(new Date()))
        {
            throw new UnauthenticatedRequestException("Token has expired");
        }

        // if token was already renewed
        if(authTokenFromDb.getLastUpdatedOn().after(tokenDetails.getLastUpdatedOn()))
        {
            TokenDetails newTokenDetails = new TokenDetails()
                .setAuthToken(token)
                .setAuthTokenExpiresAt(authTokenFromDb.getExpiresAt())
                .setRole(authTokenFromDb.getRole())
                .setUserDetails(tokenDetails.getUserDetails())
                .setLastUpdatedOn(authTokenFromDb.getLastUpdatedOn());
            tokenCache.put(token, newTokenDetails);
            return;
        }

        long timeoutSec = sessionTimeoutSec;
        Date newExpiresAt = new Date(System.currentTimeMillis() + timeoutSec * 1000);
        Date now = new Date();

        boolean res = authTokenRepository.updateExpiresAt(token, now, newExpiresAt, now);

        if(!res)
        {
            throw new UnauthenticatedRequestException("Failed to renew token");
        }

        tokenDetails.setAuthTokenExpiresAt(newExpiresAt);
        tokenDetails.setLastUpdatedOn(now);
        tokenCache.put(token, tokenDetails);
    }

    public void revokeToken()
    {
        logger.debug("Revoking token");
        
        String token = UserContext.getCurrentUser().getAuthToken();
        authTokenRepository.deleteByToken(token);
        tokenCache.remove(token);
    }

    private void cleanupExpiredTokens()
    {
        logger.debug("Cleaning up expired tokens");
        int deletedCount = authTokenRepository.deleteExpiredTokens(new Date());

        if(deletedCount > 0)
        {
            logger.info("Deleted {} expired tokens", deletedCount);
        }
    }
}
