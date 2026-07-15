package com.webutils.testapp.otp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webutils.common.response.BaseResponse;

import jakarta.validation.Valid;

/**
 * Accepts OTP demo form posts; {@code @Otp} validation runs via bean validation.
 */
@RestController
@RequestMapping("/api/testapp/otp-demo")
public class OtpDemoController
{
	private static final Logger logger = LogManager.getLogger(OtpDemoController.class);

	@PostMapping("/submit")
	public BaseResponse submit(@RequestBody @Valid OtpDemoModel model)
	{
		logger.info("OTP demo submit accepted (email/mobile verified)");
		return new BaseResponse();
	}
}
