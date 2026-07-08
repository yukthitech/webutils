package com.webutils.services.form.token;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.webutils.common.repo.IMissingTableRepository;
import com.webutils.services.common.ExecutionService;
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
	 * Generates a random token, persists the value with expiry, and returns the token.
	 * @param value value to associate with the token
	 * @param expirySec time-to-live in seconds from now
	 */
	public String saveToken(String value, long expirySec)
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
		entity.setExpiresAt(expiresAt);
		entity.setCreatedOn(now);
		tokenRepository.save(entity);

		return token;
	}

	/**
	 * Returns the value for the given token if it exists and has not expired.
	 */
	public String fetchToken(String token)
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

		return entity.getValue();
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
