package com.webutils.services.form.token;

/**
 * Result of looking up a form token without collapsing expiry into not-found.
 */
public class TokenLookupResult
{
	public enum Status
	{
		NOT_FOUND,
		EXPIRED,
		VALID
	}

	private final Status status;

	private final TokenEntity entity;

	private TokenLookupResult(Status status, TokenEntity entity)
	{
		this.status = status;
		this.entity = entity;
	}

	public static TokenLookupResult notFound()
	{
		return new TokenLookupResult(Status.NOT_FOUND, null);
	}

	public static TokenLookupResult expired(TokenEntity entity)
	{
		return new TokenLookupResult(Status.EXPIRED, entity);
	}

	public static TokenLookupResult valid(TokenEntity entity)
	{
		return new TokenLookupResult(Status.VALID, entity);
	}

	public Status getStatus()
	{
		return status;
	}

	public TokenEntity getEntity()
	{
		return entity;
	}

	public String getValue()
	{
		return entity == null ? null : entity.getValue();
	}

	public boolean isValid()
	{
		return status == Status.VALID;
	}

	public boolean isExpired()
	{
		return status == Status.EXPIRED;
	}

	public boolean isNotFound()
	{
		return status == Status.NOT_FOUND;
	}
}
