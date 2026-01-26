package com.webutils.common.form.otp;

import com.webutils.common.ValueWithToken;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class OtpVerification extends ValueWithToken
{
	/**
	 * Value to be verified - mobile number, email id, etc.
	 */
	private String valueToVerify;
}
