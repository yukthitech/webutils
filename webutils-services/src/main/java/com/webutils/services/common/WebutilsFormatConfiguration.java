package com.webutils.services.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "app.webutils")
@Data
public class WebutilsFormatConfiguration
{
	private String dateFormat = "dd-MMM-yyyy";
	private String numberFormat = "#,##0.##";

	public SimpleDateFormat newDateFormat()
	{
		return new SimpleDateFormat(dateFormat);
	}

	public DecimalFormat newNumberFormat()
	{
		return new DecimalFormat(numberFormat);
	}
}
