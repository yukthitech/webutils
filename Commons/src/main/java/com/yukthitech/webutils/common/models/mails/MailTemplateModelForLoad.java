package com.yukthitech.webutils.common.models.mails;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Mail template model extension, which can load properties from property xml file.
 * @author akiran
 */
public class MailTemplateModelForLoad extends MailTemplateModel
{
	/**
	 * Loads the html content from specified template file.
	 * @param file File to load.
	 */
	public void setTemplateFile(String file)
	{
		try
		{
			InputStream is = MailTemplateModelForLoad.class.getResourceAsStream(file);
			String contentHtml = IOUtils.toString(is);
			
			super.setContentTemplate(contentHtml);
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while loading file: {}", file);
		}
	}
}
