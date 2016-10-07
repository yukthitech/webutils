package com.yukthi.webutils.mail.template;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Used to represent attachment details in {@link MailTemplateConfig}.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AttachmentConfig
{
	/**
	 * Name of the image, which should be used as content-id for attachment.
	 * @return name of attachment.
	 */
	public String name();
	
	/**
	 * Description of the attachment.
	 * @return Description of the attachment.
	 */
	public String description();
	
	/**
	 * Is the attachment is an image.
	 * @return true if attachment is image.
	 */
	public boolean image();
}
