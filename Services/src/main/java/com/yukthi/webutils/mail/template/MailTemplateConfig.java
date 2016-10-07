package com.yukthi.webutils.mail.template;

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
	 * Details of attachments that are going to be part of the mail.
	 * @return attachment details.
	 */
	public AttachmentConfig[] attachments() default {};
}
