package com.webutils.services.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webutils.common.IWebUtilsConstants;
import com.webutils.common.auth.NoAuthentication;
import com.webutils.common.payment.CreatePaymentOrderRequest;
import com.webutils.common.payment.PaymentOrderResponse;
import com.webutils.common.payment.PaymentWebhookEvent;
import com.webutils.common.response.BaseResponse;
import com.webutils.common.response.BasicReadResponse;
import com.webutils.services.common.InvalidRequestException;
import com.webutils.services.payment.razorpay.RazorpayWebhookPayload;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payment")
public class PaymentController
{
	@Autowired
	private PaymentService paymentService;

	@PostMapping("/orders")
	public PaymentOrderResponse createOrder(@RequestBody @Valid CreatePaymentOrderRequest request)
	{
		return paymentService.createOrder(request);
	}

	@GetMapping("/orders/{id}")
	public BasicReadResponse<PaymentOrderResponse> getOrder(@PathVariable("id") Long id)
	{
		return new BasicReadResponse<>(paymentService.getOrder(id));
	}

	@NoAuthentication
	@PostMapping("/webhook/razorpay")
	public BaseResponse razorpayWebhook(
			@RequestBody String payload,
			@RequestHeader(value = "X-Razorpay-Signature", required = false) String signature)
	{
		RazorpayWebhookPayload razorpayPayload;

		try
		{
			razorpayPayload = IWebUtilsConstants.OBJECT_MAPPER.readValue(payload, RazorpayWebhookPayload.class);
		}
		catch(JsonProcessingException ex)
		{
			throw new InvalidRequestException("Invalid Razorpay webhook JSON: {}", ex.getMessage());
		}

		PaymentWebhookEvent event = RazorpayWebhookPayload.toEvent(razorpayPayload);
		paymentService.processWebhook(event, payload, signature);
		return new BaseResponse();
	}
}
