package com.yukthitech.webutils.client.actionplan;

import com.yukthi.utils.fmarker.FreeMarkerEngine;

/**
 * Template processor based on free marker engine.
 * @author akiran
 */
public class FreeMarkerTemplateProcessor implements ITemplateProcessor
{
	/**
	 * To parse free marker templates.
	 */
	private FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine();

	@Override
	public String processTemplate(String name, String template, Object context)
	{
		return freeMarkerEngine.processTemplate(name, template, context);
	}
}
