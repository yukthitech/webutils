package com.yukthi.webutils.mail.template;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates the target field to be available field for consumption in mail templates.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MailConfigField
{
	/**
	 * Description of the field.
	 * @return Description of the field.
	 */
	public String description();
}
