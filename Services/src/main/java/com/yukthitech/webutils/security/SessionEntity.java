package com.yukthitech.webutils.security;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.yukthitech.persistence.annotations.DataType;
import com.yukthitech.persistence.annotations.DataTypeMapping;
import com.yukthitech.persistence.annotations.Index;
import com.yukthitech.persistence.annotations.Indexes;
import com.yukthitech.persistence.annotations.NotUpdateable;
import com.yukthitech.persistence.conversion.impl.JsonConverter;
import com.yukthitech.webutils.common.UserDetails;

/**
 * Entity to maintain sessions.
 * @author akiran
 */
@Indexes({
	@Index(name = "SESSION_TOKEN", fields = {"sessionToken"})
	})
@Table(name = "WEBUTILS_USER_SESSION")
public class SessionEntity
{
	/**
	 * Primary key of the entity.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	/**
	 * Session token, which would be passed to and from user for maintaining the session.
	 */
	@Column(name = "SESSION_TOKEN", nullable = false, length = 500)
	private String sessionToken;
	
	/**
	 * User details returned by authentication service during authentication. Which would be available
	 * on server side in the current session.
	 */
	@Column(name = "USER_DETAILS", nullable = false, length = 2000)
	@DataTypeMapping(type = DataType.STRING, converterType = JsonConverter.class)
	private UserDetails userDetails;
	
	/**
	 * User id of the session.
	 */
	@Column(name = "USER_ID", nullable = false)
	private Long userId;
	
	/**
	 * Last time when an api was accessed using this session.
	 */
	@Column(name = "LAST_ACCESSED_ON")
	@DataTypeMapping(type = DataType.DATE_TIME)
	private Date lastAccessedOn;

	/**
	 * Time when session is created. Used for audit purpose only.
	 */
	@NotUpdateable
	@Column(name = "CREATED_ON")
	@DataTypeMapping(type = DataType.DATE_TIME)
	private Date createdOn;
	
	/**
	 * Instantiates a new session entity.
	 */
	public SessionEntity()
	{}
	
	/**
	 * Instantiates a new session entity.
	 *
	 * @param id the id
	 * @param sessionToken the session token
	 * @param userDetails the user details
	 * @param userId the user id
	 * @param lastAccessedOn the expires on
	 * @param createdOn the created on
	 */
	public SessionEntity(Long id, String sessionToken, UserDetails userDetails, Long userId, Date lastAccessedOn, Date createdOn)
	{
		this.id = id;
		this.sessionToken = sessionToken;
		this.userDetails = userDetails;
		this.userId = userId;
		this.lastAccessedOn = lastAccessedOn;
		this.createdOn = createdOn;
	}

	/**
	 * Gets the primary key of the entity.
	 *
	 * @return the primary key of the entity
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * Sets the primary key of the entity.
	 *
	 * @param id the new primary key of the entity
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * Gets the session token, which would be passed to and from user for maintaining the session.
	 *
	 * @return the session token, which would be passed to and from user for maintaining the session
	 */
	public String getSessionToken()
	{
		return sessionToken;
	}

	/**
	 * Sets the session token, which would be passed to and from user for maintaining the session.
	 *
	 * @param sessionToken the new session token, which would be passed to and from user for maintaining the session
	 */
	public void setSessionToken(String sessionToken)
	{
		this.sessionToken = sessionToken;
	}

	/**
	 * Gets the user details returned by authentication service during authentication. Which would be available on server side in the current session.
	 *
	 * @return the user details returned by authentication service during authentication
	 */
	public UserDetails getUserDetails()
	{
		return userDetails;
	}

	/**
	 * Sets the user details returned by authentication service during authentication. Which would be available on server side in the current session.
	 *
	 * @param userDetails the new user details returned by authentication service during authentication
	 */
	public void setUserDetails(UserDetails userDetails)
	{
		this.userDetails = userDetails;
	}

	/**
	 * Gets the last time when an api was accessed using this session.
	 *
	 * @return the last time when an api was accessed using this session
	 */
	public Date getLastAccessedOn()
	{
		return lastAccessedOn;
	}

	/**
	 * Sets the last time when an api was accessed using this session.
	 *
	 * @param lastAccessedOn the new last time when an api was accessed using this session
	 */
	public void setLastAccessedOn(Date lastAccessedOn)
	{
		this.lastAccessedOn = lastAccessedOn;
	}

	/**
	 * Gets the time when session is created. Used for audit purpose only.
	 *
	 * @return the time when session is created
	 */
	public Date getCreatedOn()
	{
		return createdOn;
	}

	/**
	 * Sets the time when session is created. Used for audit purpose only.
	 *
	 * @param createdOn the new time when session is created
	 */
	public void setCreatedOn(Date createdOn)
	{
		this.createdOn = createdOn;
	}

	/**
	 * Gets the user id of the session.
	 *
	 * @return the user id of the session
	 */
	public Long getUserId()
	{
		return userId;
	}

	/**
	 * Sets the user id of the session.
	 *
	 * @param userId the new user id of the session
	 */
	public void setUserId(Long userId)
	{
		this.userId = userId;
	}
}
