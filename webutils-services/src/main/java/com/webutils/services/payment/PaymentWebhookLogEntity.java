package com.webutils.services.payment;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "PAYMENT_WEBHOOK_LOG")
public class PaymentWebhookLogEntity
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@ManyToOne
	@Column(name = "PAYMENT_ORDER_ID")
	private PaymentOrderEntity paymentOrder;

	@Column(name = "EVENT_TYPE", length = 100)
	private String eventType;

	@Column(name = "PAYLOAD", nullable = false)
	private String payload;

	@Column(name = "SIGNATURE", length = 500)
	private String signature;

	@Column(name = "SIGNATURE_VALID")
	private Boolean signatureValid;

	@Column(name = "PROCESSED")
	private Boolean processed = false;

	@Column(name = "CREATED_ON", nullable = false)
	private Date createdOn = new Date();
}
