package com.yukthitech.webutils.services.task;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark a method to be executed as repetitive task.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RepetitiveTask
{
	/**
	 * Interval at which this method should be executed. 
	 * @return interval at which method should be executed.
	 */
	public long interval();
	
	/**
	 * Initial delay after which first time execution should happen after startup. Negative value indicates
	 * not to execute during startup. Defaults to -1.
	 * @return initial delay
	 */
	public long initialDelay() default -1;
}
