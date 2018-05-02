package com.yukthitech.webutils.alerts.agent.system;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark and provide meta information about application alert processor.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SystemAlertProcessor
{
	/**
	 * Name of the alert that target method like to process.
	 * @return name of alert supported by this processor.
	 */
	public String name();
	
	/**
	 * If true, then target method would be invoked only for confirmation alerts.
	 * @return true if only confirmation alerts are supported. 
	 */
	public boolean confirmation() default false;
	
	/**
	 * Optional free marker condition that can be used on target method. If specified, the target 
	 * method will be evaluate to true, only if condition evaluates to true. The parameters of the 
	 * method can be accessed in condition using p0, p1.. pn or a0, a1... an.
	 * @return free marker condition string.
	 */
	public String condition() default "";
}
