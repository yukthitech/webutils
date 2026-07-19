package com.webutils.services.form.token;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "FORM_TOKEN")
public class TokenEntity
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "TOKEN", nullable = false)
	private String token;

	@Column(name = "VALUE", nullable = false)
	private String value;

	/**
	 * Server-side purpose (e.g. LOGIN, RESET, VERIFICATION). Not exposed to the client;
	 * verified when the token is consumed. Nullable for non-OTP tokens such as captcha.
	 */
	@Column(name = "PURPOSE", length = 50)
	private String purpose;

	/**
	 * User the token was issued for. Verified on consume so a token cannot be reused
	 * across users. Nullable for non-OTP tokens such as captcha.
	 */
	@Column(name = "USER_ID")
	private Long userId;

	@Column(name = "EXPIRES_AT", nullable = false)
	private Date expiresAt;

	@Column(name = "CREATED_ON", nullable = false)
	private Date createdOn;
}
