package com.webutils.services.payment.razorpay;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.webutils.common.payment.PaymentWebhookEvent;
import com.webutils.services.common.InvalidRequestException;

import lombok.Data;

/**
 * Razorpay webhook event envelope.
 * @see <a href="https://razorpay.com/docs/webhooks/payloads/payments/">Razorpay payment webhook payloads</a>
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RazorpayWebhookPayload
{
	private String event;

	private Payload payload;

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Payload
	{
		private PaymentWrapper payment;
	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class PaymentWrapper
	{
		private PaymentEntity entity;
	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class PaymentEntity
	{
		private String id;

		@JsonProperty("order_id")
		private String orderId;
	}

	public static PaymentWebhookEvent toEvent(RazorpayWebhookPayload payload)
	{
		if(payload == null)
		{
			throw new InvalidRequestException("Razorpay webhook payload is required");
		}

		PaymentEntity payment = payload.getPayload() != null && payload.getPayload().getPayment() != null
				? payload.getPayload().getPayment().getEntity()
				: null;

		if(payment == null)
		{
			throw new InvalidRequestException("Razorpay webhook missing payload.payment.entity");
		}

		if(StringUtils.isBlank(payment.getOrderId()))
		{
			throw new InvalidRequestException("Razorpay webhook missing order_id");
		}

		if(StringUtils.isBlank(payment.getId()))
		{
			throw new InvalidRequestException("Razorpay webhook missing payment id");
		}

		return new PaymentWebhookEvent()
				.setEventType(StringUtils.isNotBlank(payload.getEvent()) ? payload.getEvent() : "payment.captured")
				.setGatewayOrderId(payment.getOrderId())
				.setGatewayPaymentId(payment.getId());
	}

}
