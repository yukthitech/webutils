package com.webutils.services.payment;

import com.webutils.common.Optional;
import com.yukthitech.persistence.ICrudRepository;

@Optional
public interface IPaymentWebhookLogRepository extends ICrudRepository<PaymentWebhookLogEntity>
{
}
