package com.yukthi.webutils.mail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yukthi.ccg.xml.XMLBeanParser;
import com.yukthi.utils.exceptions.InvalidStateException;

/**
 * Represents a email template file which can have multiple email templates
 * 
 * @author akiran
 */
public class EmailTemplateService
{
	private Map<String, EmailTemplate> templates = new HashMap<>();
	
	/**
	 * Loads templates from specified resources
	 * @param resources
	 */
	public void setTemplateResources(List<String> resources)
	{
		for(String resource : resources)
		{
			try
			{
				XMLBeanParser.parse(EmailTemplateService.class.getResourceAsStream(resource), this);
			}catch(Exception ex)
			{
				throw new InvalidStateException(ex, "An error occurred while loading resoure - {}", resource);
			}
		}
	}
	
	public void setEmailTemplate(EmailTemplate template)
	{
		this.templates.put(template.getName(), template);
	}

	/**
	 * Fetches email template with specified name
	 * @param name
	 * @return
	 */
	public EmailTemplate getEmailTemplate(String name)
	{
		return templates.get(name);
	}
}
