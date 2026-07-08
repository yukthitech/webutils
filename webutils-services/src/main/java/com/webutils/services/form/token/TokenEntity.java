package com.webutils.services.form.token;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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

	@Column(name = "EXPIRES_AT", nullable = false)
	private Date expiresAt;

	@Column(name = "CREATED_ON", nullable = false)
	private Date createdOn;
}
