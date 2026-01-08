package com.webutils.services.token;

import java.util.Date;

import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.persistence.repository.annotations.Field;
import com.yukthitech.persistence.repository.annotations.Operator;

public interface IAuthTokenRepository extends ICrudRepository<AuthTokenEntity>
{
    AuthTokenEntity fetchByToken(@Condition("token") String token);

    boolean deleteByToken(@Condition("token") String token);

    boolean updateExpiresAt(
        @Condition("token") String token, 
        @Condition(value = "expiresAt", op = Operator.GT) Date curTime,
        @Field("expiresAt") Date newExpiresAt,
        @Field("lastUpdatedOn") Date lastUpdatedOn);

    int deleteExpiredTokens(@Condition(value = "expiresAt", op = Operator.LT) Date curTime);
}
