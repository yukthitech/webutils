package com.webutils.services.payment;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.webutils.common.UserDetails;
import com.webutils.common.payment.CreatePaymentOrderRequest;
import com.webutils.common.payment.PaymentOrderResponse;
import com.webutils.common.payment.PaymentWebhookEvent;
import com.webutils.services.auth.UserContext;
import com.webutils.services.common.InvalidRequestException;
import com.webutils.services.user.UserEntity;

@Service
public class PaymentService
{
	private static final Logger logger = LogManager.getLogger(PaymentService.class);

	@Autowired
	private IPaymentOrderRepository paymentOrderRepository;

	@Autowired
	private IPaymentWebhookLogRepository webhookLogRepository;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Value("${webutils.payment.razorpay.keyId:}")
	private String razorpayKeyId;

	@Value("${webutils.payment.razorpay.keySecret:}")
	private String razorpayKeySecret;

	@Value("${webutils.payment.razorpay.webhookSecret:}")
	private String webhookSecret;

	@Value("${webutils.payment.razorpay.currency:INR}")
	private String defaultCurrency;

	@Value("${app.devEnvironment:false}")
	private boolean isDevEnvironment;

	public PaymentOrderResponse createOrder(CreatePaymentOrderRequest request)
	{
		requirePaymentConfig();

		UserDetails current = UserContext.getCurrentUser();
		String currency = StringUtils.isNotBlank(request.getCurrency()) ? request.getCurrency() : defaultCurrency;

		// Skeleton: local order id. Real Razorpay Orders API can replace this when keys are configured.
		String gatewayOrderId = "order_" + UUID.randomUUID().toString().replace("-", "").substring(0, 14);

		PaymentOrderEntity order = new PaymentOrderEntity()
				.setGatewayOrderId(gatewayOrderId)
				.setAmount(request.getAmount())
				.setCurrency(currency)
				.setStatus(PaymentOrderStatus.CREATED.name())
				.setReceipt(request.getReceipt())
				.setMetadata(request.getMetadata())
				.setUser(new UserEntity(current.getId()))
				.setCreatedOn(new Date());

		paymentOrderRepository.save(order);
		logger.info("Created payment order id={} gatewayOrderId={} amount={}", order.getId(), gatewayOrderId, request.getAmount());

		return toResponse(order);
	}

	public PaymentOrderResponse getOrder(Long id)
	{
		requirePaymentConfig();

		PaymentOrderEntity order = paymentOrderRepository.findById(id);

		if(order == null)
		{
			throw new InvalidRequestException("Payment order not found: {}", id);
		}

		return toResponse(order);
	}

	/**
	 * Processes a parsed payment webhook. Verifies HMAC signature unless running in a dev environment.
	 */
	public void processWebhook(PaymentWebhookEvent event, String rawPayload, String signature)
	{
		requirePaymentConfig();

		boolean signatureValid = verifySignature(rawPayload, signature);

		PaymentWebhookLogEntity log = new PaymentWebhookLogEntity()
				.setPayload(rawPayload)
				.setSignature(signature)
				.setSignatureValid(signatureValid)
				.setEventType(event.getEventType())
				.setCreatedOn(new Date());

		if(!signatureValid)
		{
			webhookLogRepository.save(log);
			throw new InvalidRequestException("Invalid webhook signature");
		}

		String gatewayOrderId = event.getGatewayOrderId();
		String gatewayPaymentId = event.getGatewayPaymentId();

		PaymentOrderEntity order = null;

		if(StringUtils.isNotBlank(gatewayOrderId))
		{
			order = paymentOrderRepository.fetchByGatewayOrderId(gatewayOrderId);
		}

		if(order != null)
		{
			log.setPaymentOrder(order);
		}

		webhookLogRepository.save(log);

		if(order == null)
		{
			logger.warn("Webhook for unknown gateway order id: {}", gatewayOrderId);
			return;
		}

		markPaid(order, gatewayPaymentId);
		log.setProcessed(true);
		webhookLogRepository.update(log);
	}

	private void requirePaymentConfig()
	{
		if(StringUtils.isBlank(razorpayKeyId) || StringUtils.isBlank(razorpayKeySecret) || StringUtils.isBlank(webhookSecret))
		{
			throw new InvalidRequestException(
					"Payment is not configured: webutils.payment.razorpay.keyId, keySecret and webhookSecret are required");
		}
	}

	private void markPaid(PaymentOrderEntity order, String gatewayPaymentId)
	{
		if(PaymentOrderStatus.PAID.name().equals(order.getStatus()))
		{
			logger.info("Payment order id={} already PAID; ignoring duplicate", order.getId());
			return;
		}

		Date now = new Date();
		paymentOrderRepository.markPaid(PaymentOrderStatus.PAID.name(), gatewayPaymentId, now, now, order.getId());

		order.setStatus(PaymentOrderStatus.PAID.name());
		order.setGatewayPaymentId(gatewayPaymentId);
		order.setPaidOn(now);

		Long userId = order.getUser() != null ? order.getUser().getId() : null;
		eventPublisher.publishEvent(new PaymentSuccessEvent(
				this, order.getId(), order.getGatewayOrderId(),
				order.getAmount(), order.getCurrency(), userId, order.getMetadata()));

		logger.info("Payment order id={} marked PAID", order.getId());
	}

	private boolean verifySignature(String payload, String signature)
	{
		if(isDevEnvironment)
		{
			return true;
		}

		if(StringUtils.isBlank(signature) || StringUtils.isBlank(payload))
		{
			return false;
		}

		try
		{
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
			byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
			String expected = bytesToHex(hash);
			return expected.equalsIgnoreCase(signature) || expected.equalsIgnoreCase(stripPrefix(signature));
		}
		catch(Exception ex)
		{
			logger.error("Failed to verify webhook signature", ex);
			return false;
		}
	}

	private static String stripPrefix(String signature)
	{
		int idx = signature.indexOf('=');
		return idx >= 0 ? signature.substring(idx + 1) : signature;
	}

	private static String bytesToHex(byte[] bytes)
	{
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for(byte b : bytes)
		{
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}

	private PaymentOrderResponse toResponse(PaymentOrderEntity order)
	{
		return new PaymentOrderResponse()
				.setId(order.getId())
				.setGatewayOrderId(order.getGatewayOrderId())
				.setAmount(order.getAmount())
				.setCurrency(order.getCurrency())
				.setStatus(order.getStatus())
				.setKeyId(razorpayKeyId)
				.setMetadata(order.getMetadata())
				.setReceipt(order.getReceipt());
	}
}
