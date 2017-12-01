package com.yukthitech.webutils.mail.template;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates the field content should be sent as an attachment.
 * Only following fields can be marked as attachment - String, byte[], java.io.File, java.awt.Image 
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MailAttachment
{
	/**
	 * File name of the attachment.
	 * @return name of the attachment.
	 */
	public String name();
	
	/**
	 * Description of the field.
	 * @return Description of the field.
	 */
	public String description();
	
	/**
	 * Indicates this is image attachment. For Image fields, the attachment will be considered as attachment automatically. 
	 * Defaults to false.
	 * @return return true if the attachment is an image.
	 */
	public boolean image() default false;
}
