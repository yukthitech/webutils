package com.webutils.common.payment;

import java.util.Map;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request to create a payment gateway order.
 */
@Data
public class CreatePaymentOrderRequest
{
	/**
	 * Amount in smallest currency unit (paise for INR), or major units depending on gateway config.
	 * For Razorpay INR this is amount in paise.
	 * 
	 * This field in general will be in Rupees.
	 */
	@NotNull
	@Min(1)
	private Long amount;

	/**
	 * ISO currency code. Defaults to configured currency when blank.
	 */
	private String currency = "INR";

	/**
	 * Optional app-specific metadata (e.g. employerId, purpose).
	 */
	private Map<String, Object> metadata;

	/**
	 * Optional receipt / reference id from the calling application.
	 */
	private String receipt;
}
