package com.yukthitech.webutils.services.freemarker;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

/**
 * Default functions for free marker templates.
 * @author akiran
 */
public class DefaultFunctions
{
	/**
	 * Fetches current data in specified format.
	 * @param format Format to use
	 * @return Formatted current date
	 */
	@FreemarkerMethod
	public static String now(String format)
	{
		SimpleDateFormat dataFormat = new SimpleDateFormat(format);
		return dataFormat.format(new Date());
	}
	
	/**
	 * Adds specified number of days to current date and returns in specified format.
	 * @param days Days to add.
	 * @param format Format to use
	 * @return Formatted date
	 */
	@FreemarkerMethod
	public static String date(int days, String format)
	{
		SimpleDateFormat dataFormat = new SimpleDateFormat(format);
		return dataFormat.format(DateUtils.addDays(new Date(), days));
	}
}
