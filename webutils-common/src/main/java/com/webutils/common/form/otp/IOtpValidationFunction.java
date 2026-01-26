package com.webutils.common.form.otp;

public interface IOtpValidationFunction
{
	public void validate(VerificationType verificationType, OtpVerification otp);
}
