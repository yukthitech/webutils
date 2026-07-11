package com.webutils.services.form.token;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.webutils.common.repo.IMissingTableRepository;
import com.webutils.services.common.ExecutionService;
import com.webutils.services.common.InvalidRequestException;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

import jakarta.annotation.PostConstruct;

@Service
public class TokenManager
{
	private static final Logger logger = LogManager.getLogger(TokenManager.class);

	@Autowired
	private ITokenRepository tokenRepository;

	@Autowired
	private ExecutionService executionService;

	@Value("${webutils.form.token.cleanup.interval.seconds:1800}")
	private long cleanupIntervalSec;

	private boolean disabled = false;

	@PostConstruct
	private void init()
	{
		disabled = (tokenRepository instanceof IMissingTableRepository);

		if(disabled)
		{
			logger.info("TokenManager is disabled as required table is not defined");
			return;
		}

		executionService.scheduleRepeatedTask("TokenManager.cleanupTokens",
				this::cleanupTokens, cleanupIntervalSec, TimeUnit.SECONDS);
	}

	public boolean isDisabled()
	{
		return disabled;
	}

	/**
	 * Generates a random token without purpose or user (e.g. captcha).
	 */
	public String saveToken(String value, long expirySec)
	{
		return saveToken(value, expirySec, null, null);
	}

	/**
	 * Generates a random token, persists value + optional purpose/userId with expiry, and returns the token id.
	 * Purpose and userId are stored server-side only and are not part of the client-facing token string.
	 */
	public String saveToken(String value, long expirySec, String purpose, Long userId)
	{
		if(disabled)
		{
			throw new InvalidStateException("Token manager is disabled");
		}

		String token = UUID.randomUUID().toString();
		Date now = new Date();
		Date expiresAt = new Date(now.getTime() + expirySec * 1000);

		TokenEntity entity = new TokenEntity();
		entity.setToken(token);
		entity.setValue(value);
		entity.setPurpose(purpose);
		entity.setUserId(userId);
		entity.setExpiresAt(expiresAt);
		entity.setCreatedOn(now);
		tokenRepository.save(entity);

		return token;
	}

	/**
	 * Returns the value for the given token if it exists and has not expired.
	 * Does not check purpose or user (for captcha and other unbound tokens).
	 */
	public String fetchToken(String token)
	{
		TokenEntity entity = fetchValidEntity(token);
		return entity == null ? null : entity.getValue();
	}

	/**
	 * Returns the value for the given token only if it exists, is unexpired,
	 * and both purpose and userId match the stored values.
	 */
	public TokenEntity fetchTokenEntity(String token, String purpose, Long userId)
	{
		if(StringUtils.isBlank(purpose))
		{
			throw new InvalidArgumentException("Purpose is required for token fetch");
		}

		if(userId == null)
		{
			throw new InvalidArgumentException("User id is required for token fetch");
		}

		TokenEntity entity = fetchValidEntity(token);

		if(entity == null)
		{
			return null;
		}

		if(!purpose.equals(entity.getPurpose()))
		{
			logger.debug("Token purpose mismatch for token={}, Entity Token Purpose={}, Requested Token Purpose={}", token, entity.getPurpose(), purpose);
			throw new InvalidRequestException("Token mismatch");
		}

		if(!Objects.equals(userId, entity.getUserId()))
		{
			logger.debug("Token user mismatch for token={}, Entity User ID={}, Requested User ID={}", token, entity.getUserId(), userId);
			throw new InvalidRequestException("Token mismatch");
		}

		return entity;
	}

	public void deleteToken(long tokenId)
	{
		tokenRepository.deleteById(tokenId);
	}

	private TokenEntity fetchValidEntity(String token)
	{
		if(disabled)
		{
			throw new InvalidStateException("Token manager is disabled");
		}

		TokenEntity entity = tokenRepository.fetchByToken(token);

		if(entity == null || entity.getExpiresAt().before(new Date()))
		{
			return null;
		}

		return entity;
	}

	private void cleanupTokens()
	{
		logger.debug("Cleaning up expired form tokens");
		int deletedCount = tokenRepository.deleteExpiredTokens(new Date());

		if(deletedCount > 0)
		{
			logger.info("Deleted {} expired form tokens", deletedCount);
		}
	}
}
