package com.webutils.testapp.otp;

import com.webutils.common.form.annotations.Model;
import com.webutils.common.form.otp.Otp;
import com.webutils.common.form.otp.OtpVerification;
import com.webutils.common.form.otp.VerificationType;

import lombok.Data;

/**
 * Demo model for OTP / verification input widget.
 */
@Data
@Model(name = "OtpDemoModel")
public class OtpDemoModel
{
	@Otp(type = VerificationType.EMAIL)
	private OtpVerification emailOtp;

	@Otp(type = VerificationType.MOBILE)
	private OtpVerification phoneOtp;
}
