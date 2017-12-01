package com.yukthitech.webutils.security;

import java.util.Date;

import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.persistence.repository.annotations.Field;
import com.yukthitech.persistence.repository.annotations.Operator;

/**
 * Repository to maintain sessions.
 * @author akiran
 */
public interface ISessionRepository extends ICrudRepository<SessionEntity>
{
	/**
	 * Fetches session details for specified session token.
	 * @param sessionToken Session token for which user details needs to be fetched.
	 * @return Matching user details.
	 */
	public SessionEntity fetchByToken(@Condition("sessionToken") String sessionToken);
	
	/**
	 * Deletes the session with specified token.
	 * @param sessionToken token of session to be deleted.
	 */
	public void deleteByToken(@Condition("sessionToken") String sessionToken);
	
	/**
	 * Updates expiry time of specified session token.
	 * @param lastAccessedOn Last access time.
	 * @param sessionToken Session
	 * @return true if update was successful.
	 */
	public boolean updateExpiryTime(@Field("lastAccessedOn") Date lastAccessedOn, @Condition("sessionToken") String sessionToken);
	
	/**
	 * Deletes all sessions which are older than specified accessed on time.
	 * @param lastAccessedOn Access time before which which sessions should be deletes.
	 * @return Number of sessions deleted.
	 */
	public int deleteOldSessions(@Condition(value = "lastAccessedOn", op = Operator.LT) Date lastAccessedOn);
}
