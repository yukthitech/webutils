package com.webutils.services.form.token;

import java.util.Date;

import com.webutils.common.Optional;
import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.persistence.repository.annotations.Operator;

@Optional
public interface ITokenRepository extends ICrudRepository<TokenEntity>
{
	TokenEntity fetchByToken(@Condition("token") String token);

	int deleteExpiredTokens(@Condition(value = "expiresAt", op = Operator.LT) Date curTime);
}
