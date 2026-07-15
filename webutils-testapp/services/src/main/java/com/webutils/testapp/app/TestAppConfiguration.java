package com.webutils.testapp.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.webutils.common.ConfigurationUtils;
import com.webutils.common.mail.EmailServerSettings;
import com.webutils.services.mail.EmailService;
import com.yukthitech.utils.Encryptor;

/**
 * Optional framework beans required when mail/OTP delivery is wired.
 * With {@code app.devEnvironment=true}, OTP delivery is skipped.
 */
@Configuration
public class TestAppConfiguration
{
	@Autowired
	private Environment environment;

	@Autowired
	private Encryptor encryptor;

	@Bean
	public EmailServerSettings emailServerSettings()
	{
		return ConfigurationUtils.buildConfiguration(EmailServerSettings.class, "webutils.email", environment, encryptor);
	}

	@Bean
	public EmailService emailService()
	{
		return new EmailService();
	}
}
