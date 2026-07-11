package com.webutils.services.payment;

import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.persistence.repository.annotations.Field;
import com.yukthitech.persistence.repository.annotations.UpdateFunction;

import java.util.Date;

public interface IPaymentOrderRepository extends ICrudRepository<PaymentOrderEntity>
{
	PaymentOrderEntity fetchByGatewayOrderId(@Condition("gatewayOrderId") String gatewayOrderId);

	@UpdateFunction
	boolean markPaid(
			@Field("status") String status,
			@Field("gatewayPaymentId") String gatewayPaymentId,
			@Field("paidOn") Date paidOn,
			@Field("updatedOn") Date updatedOn,
			@Condition("id") Long id);
}
