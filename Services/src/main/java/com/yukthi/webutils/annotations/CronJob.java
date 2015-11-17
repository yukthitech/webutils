package com.yukthi.webutils.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * Used to mark a class as Cron job
 * @author akiran
 */
@Component
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
