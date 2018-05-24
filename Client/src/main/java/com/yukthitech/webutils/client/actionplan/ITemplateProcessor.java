package com.yukthitech.webutils.client.actionplan;

/**
 * Used to process templates.
 * @author akiran
 */
public interface ITemplateProcessor
{
	/**
	 * Process the templates with specified context.
	 * @param name name of template
	 * @param template template to process
	 * @param context context for processing.
	 * @return resultant string
	 */
	public String processTemplate(String name, String template, Object context);
}
