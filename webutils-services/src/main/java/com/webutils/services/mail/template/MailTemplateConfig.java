package com.webutils.services.mail.template;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the type as mail template configuration provider. Whose instance will be provided during 
 * email template processing.
 * 
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MailTemplateConfig
{
	/**
	 * Name of the template.
	 * @return Name of the template.
	 */
	public String name();
	
	/**
	 * Description of the template.
	 * @return Description of the template.
	 */
	public String description();
	
	/**
	 * Indicates if this mail configuration is for notification. Defaults to false.
	 * @return True if this configuration represents notification.
	 */
	public boolean notification() default false;
	
	/**
	 * For notification based configurations, this flag indicates if this is mandatory notification
	 * or not. Defaults to true (that is optional by default).
	 * Only optional notifications can be disabled/enabled by users. 
	 * @return true if this is an optional notification.
	 */
	public boolean optional() default true;
	
	/**
	 * Indicates whether this notification is enabled by default or not.
	 * @return true if this should be enabled by default.
	 */
	public boolean enabledByDefault() default false;
}
