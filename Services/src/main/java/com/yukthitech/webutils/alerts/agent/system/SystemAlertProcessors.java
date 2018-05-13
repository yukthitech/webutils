package com.yukthitech.webutils.alerts.agent.system;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to group list of system alert processors for single method.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SystemAlertProcessors
{
	
	/**
	 * Grouped list of processors.
	 *
	 * @return list of processors
	 */
	public SystemAlertProcessor[] value();
}
