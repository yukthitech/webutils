package com.webutils.services.common;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class WebutilsConfiguration
{
	@Bean
	public MessageSource validatorMessageSource()
	{
		ReloadableResourceBundleMessageSource msgSource = new ReloadableResourceBundleMessageSource();
		
		// below property file is part of yukthi-validator jar file
		msgSource.setBasenames("classpath:ValidationMessages");
		return msgSource;
	}
}
