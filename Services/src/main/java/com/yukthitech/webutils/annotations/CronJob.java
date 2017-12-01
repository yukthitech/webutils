package com.yukthitech.webutils.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark a class as Cron job
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CronJob
{
	/**
	 * Name of the job
	 * @return name of the job
	 */
	public String name();
	
	/**
	 * Cron expression for job scheduling
	 * @return cron expression
	 */
	public String cronExpression();
}
