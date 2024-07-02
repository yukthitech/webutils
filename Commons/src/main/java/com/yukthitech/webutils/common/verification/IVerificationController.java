package com.yukthitech.webutils.common.verification;

import com.yukthitech.webutils.common.controllers.IClientController;

/**
 * Controller for managing verification.
 * @author akiran
 */
public interface IVerificationController extends IClientController<IVerificationController>
{
	/**
	 * Generates and sends an OTP code for specified verification type and value.
	 * @param type Type of verification to be done. Eg: phone, email, etc.
	 * @param value Value to be verified. Eg: phone number, email id, etc.
	 * @return Success/failure response.
	 */
	public SendOtpResponse sendOtp(String type, String value);
	
	/**
	 * Verifies if the code specified in the request for specified request is proper or not.
	 * @param request Request to be verified
	 * @return success/failure response.
	 */
	public OtpVerificationResponse verify(OtpVerificationRequest request);
}