package com.webutils.services.payment;

import java.util.Map;

import org.springframework.context.ApplicationEvent;

/**
 * Published when a payment order transitions to PAID.
 * Consuming apps (e.g. Sethu4U) grant domain benefits on this event.
 */
public class PaymentSuccessEvent extends ApplicationEvent
{
	private static final long serialVersionUID = 1L;

	private final Long paymentOrderId;
	private final String gatewayOrderId;
	private final Long amount;
	private final String currency;
	private final Long userId;
	private final Map<String, Object> metadata;

	public PaymentSuccessEvent(Object source, Long paymentOrderId, String gatewayOrderId,
			Long amount, String currency, Long userId, Map<String, Object> metadata)
	{
		super(source);
		this.paymentOrderId = paymentOrderId;
		this.gatewayOrderId = gatewayOrderId;
		this.amount = amount;
		this.currency = currency;
		this.userId = userId;
		this.metadata = metadata;
	}

	public Long getPaymentOrderId()
	{
		return paymentOrderId;
	}

	public String getGatewayOrderId()
	{
		return gatewayOrderId;
	}

	public Long getAmount()
	{
		return amount;
	}

	public String getCurrency()
	{
		return currency;
	}

	public Long getUserId()
	{
		return userId;
	}

	public Map<String, Object> getMetadata()
	{
		return metadata;
	}
}
