package com.webutils.common.payment;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Gateway-agnostic payment webhook event after provider-specific parsing.
 */
@Data
@Accessors(chain = true)
public class PaymentWebhookEvent
{
	/** Provider event name (e.g. payment.captured). */
	private String eventType;

	/** Gateway order id (e.g. Razorpay order_xxx). */
	private String gatewayOrderId;

	/** Gateway payment id (e.g. Razorpay pay_xxx). */
	private String gatewayPaymentId;
}
