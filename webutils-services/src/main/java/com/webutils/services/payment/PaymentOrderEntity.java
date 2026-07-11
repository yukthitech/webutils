package com.webutils.services.payment;

import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.webutils.services.user.UserEntity;
import com.yukthitech.persistence.annotations.DataType;
import com.yukthitech.persistence.annotations.DataTypeMapping;
import com.yukthitech.persistence.conversion.impl.JsonConverter;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "PAYMENT_ORDER")
public class PaymentOrderEntity
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "GATEWAY_ORDER_ID", length = 100, nullable = false)
	private String gatewayOrderId;

	@Column(name = "GATEWAY_PAYMENT_ID", length = 100)
	private String gatewayPaymentId;

	@Column(name = "AMOUNT", nullable = false)
	private Long amount;

	@Column(name = "CURRENCY", length = 10, nullable = false)
	private String currency;

	@Column(name = "STATUS", length = 20, nullable = false)
	private String status;

	@Column(name = "RECEIPT", length = 100)
	private String receipt;

	@Column(name = "METADATA")
	@DataTypeMapping(type = DataType.STRING, converterType = JsonConverter.class)
	private Map<String, Object> metadata;

	@ManyToOne
	@Column(name = "USER_ID")
	private UserEntity user;

	@Column(name = "CREATED_ON", nullable = false)
	private Date createdOn = new Date();

	@Column(name = "UPDATED_ON")
	private Date updatedOn;

	@Column(name = "PAID_ON")
	private Date paidOn;
}
