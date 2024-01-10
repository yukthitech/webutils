package com.yukthitech.webutils.security;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.webutils.cache.WebutilsCacheManager;
import com.yukthitech.webutils.common.UserDetails;
import com.yukthitech.webutils.repository.IUserRepository;
import com.yukthitech.webutils.repository.UserEntity;
import com.yukthitech.webutils.services.WebutilsRepositoryFactory;
import com.yukthitech.webutils.services.job.BackgroundThreadManager;

import jakarta.annotation.PostConstruct;

/**
 * Service to manage user session.
 * @author akiran
 */
@Service
public class SessionManagementService
{
	private static Logger logger = LogManager.getLogger(SessionManagementService.class);
	
	/**
	 * Number of millis per second.
	 */
	private static final long MILLIS_PER_SEC = 1000;
	
	/**
	 * Repository factory to get session repository.
	 */
	@Autowired
	private WebutilsRepositoryFactory repositoryFactory;
	
	/**
	 * Repository for maintaining sessions.
	 */
	private ISessionRepository sessionRepository;
	
	/**
	 * Repository to access user details.
	 */
	private IUserRepository userRepository;
	
	/**
	 * Session timeout time in seconds.
	 */
	@Value("${webutils.session.timeOut.inSecs:3600}")
	private int sessionTimeout;
	
	/**
	 * Minimum time in seconds, only after which session access time will be updated. 
	 */
	@Value("${webutils.session.updateGap.inSecs:120}")
	private int sessionUpdateGap;
	
	/**
	 * Used for caching sessions.
	 */
	@Autowired
	private WebutilsCacheManager cacheManager;
	
	/**
	 * To schedule background thread which would clean old/expired sessions.
	 */
	@Autowired
	private BackgroundThreadManager backgroundThreadManager;
	
	/**
	 * Used to fetch application specific user details.
	 */
	@Autowired
	private IAuthenticationService<?> authenticationService;

	/**
	 * Post construct method to initialize repository.
	 */
	@PostConstruct
	private void init()
	{
		logger.info("Initializing sessions manager with session-time-out as {} Sec and session update time gap as - {} Sec", sessionTimeout, sessionUpdateGap);
		
		this.sessionRepository = repositoryFactory.getRepository(ISessionRepository.class);
		this.userRepository = repositoryFactory.getRepository(IUserRepository.class);
		
		this.backgroundThreadManager.scheduleWithFixedDelay(this :: cleanOldSessions, 0, sessionTimeout * 2, TimeUnit.SECONDS);
	}
	
	/**
	 * Cleans old and expired sessions.
	 */
	private void cleanOldSessions()
	{
		Date maxAccessTime = DateUtils.addSeconds(new Date(), 0 - sessionTimeout);
		
		int deletedCount = sessionRepository.deleteOldSessions(maxAccessTime);
		logger.debug("Deleted old sessions. Count: {}", deletedCount);
	}
	
	/**
	 * Starts new session with specified user details.
	 * @param userDetails User details for which new session needs to be created.
	 * @return New session id.
	 */
	public synchronized String startSession(UserDetails<?> userDetails)
	{
		Date createdOn = new Date();
		String sessionToken = UUID.randomUUID().toString();
		
		SessionEntity sessionEntity = new SessionEntity(null, sessionToken, userDetails.getUserId(), createdOn, createdOn);
		sessionEntity.setUserDetails(userDetails);
		
		if(!sessionRepository.save(sessionEntity))
		{
			throw new InvalidStateException("Failed to create new session. Please check log for more details.");
		}
		
		cacheManager.set(sessionToken, sessionEntity);
		
		return sessionToken;
	}
	
	/**
	 * Updates the last access time of the specified session. In order to avoid too many db updates, 
	 * session will be updated after configured session gap time.
	 * @param sessionEntity Entity to update.
	 */
	private void updateLastAccess(SessionEntity sessionEntity)
	{
		Date now = new Date();
		Date lastAccess = sessionEntity.getLastAccessedOn();
		
		long diff = (now.getTime() - lastAccess.getTime()) / MILLIS_PER_SEC;
		
		//ignore update call if session is accessed within specified gap
		if(diff < sessionUpdateGap)
		{
			return;
		}

		//update the session
		sessionEntity.setLastAccessedOn(now);
		
		sessionRepository.updateExpiryTime(now, sessionEntity.getSessionToken());
		cacheManager.set(sessionEntity.getSessionToken(), sessionEntity);
	}

	/**
	 * Gets unexpired session, if any, matching with specified token.
	 * @param sessionToken Token with session needs to be fetched.
	 * @return Matching session.
	 */
	private SessionEntity getSession(String sessionToken)
	{
		SessionEntity sessionEntity = (SessionEntity) cacheManager.get(sessionToken);
		
		if(sessionEntity == null)
		{
			sessionEntity = sessionRepository.fetchByToken(sessionToken);
			
			if(sessionEntity != null)
			{
				UserEntity userEntity = userRepository.findById(sessionEntity.getUserId());
				UserDetails<?> userDetails = authenticationService.toUserDetails(userEntity);
				
				sessionEntity.setUserDetails(userDetails);

				cacheManager.set(sessionToken, sessionEntity);
			}
		}
		
		if(sessionEntity == null)
		{
			return null;
		}
		
		long diff = (System.currentTimeMillis() - sessionEntity.getLastAccessedOn().getTime()) / MILLIS_PER_SEC;
		
		if(diff > sessionTimeout)
		{
			clearSession(sessionToken);
			return null;
		}
		
		updateLastAccess(sessionEntity);
		return sessionEntity;
	}
	
	/**
	 * Clears the session with specified token.
	 * @param sessionToken Session token to be cleaned.
	 */
	public void clearSession(String sessionToken)
	{
		cacheManager.get(sessionToken);
		sessionRepository.deleteByToken(sessionToken);
	}
	
	/**
	 * Fetches user details from specified user session.
	 * @param sessionToken Session from which user details needs to be fetched.
	 * @return Session user details.
	 */
	public UserDetails<?> getUserDetails(String sessionToken)
	{
		SessionEntity sessionEntity = getSession(sessionToken);
		
		if(sessionEntity == null)
		{
			return null;
		}
		
		return sessionEntity.getUserDetails();
	}
}
