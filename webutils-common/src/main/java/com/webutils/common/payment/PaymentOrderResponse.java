package com.webutils.common.payment;

import java.util.Map;

import com.webutils.common.response.BaseResponse;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Payment order details returned to the client / checkout widget.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class PaymentOrderResponse extends BaseResponse
{
	private Long id;

	/** Gateway order id (e.g. Razorpay order_xxx). */
	private String gatewayOrderId;

	private Long amount;

	private String currency;

	private String status;

	/** Public key id for checkout (e.g. Razorpay keyId). */
	private String keyId;

	private Map<String, Object> metadata;

	private String receipt;
}
