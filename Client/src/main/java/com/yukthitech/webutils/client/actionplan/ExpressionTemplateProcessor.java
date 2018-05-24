package com.yukthitech.webutils.client.actionplan;

import com.yukthitech.utils.CommonUtils;

/**
 * Template processor which can be used to replace simple expressions.
 * @author akiran
 */
public class ExpressionTemplateProcessor implements ITemplateProcessor
{
	@Override
	public String processTemplate(String name, String template, Object context)
	{
		return CommonUtils.replaceExpressions(context, template, null);
	}
}
