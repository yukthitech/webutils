package com.yukthitech.webutils.services.task;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark a method to be executed as scheduled task.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ScheduledTask
{
	/**
	 * Initial delay after which first time execution should happen after startup. Negative value indicates
	 * not to execute during startup. Defaults to -1.
	 * @return initial delay
	 */
	public long initialDelay() default -1;
	
	/**
	 * Time in "hh:mm aa" format when this method should be invoked.
	 * @return time at which task method should be executed.
	 */
	public String time();
}
