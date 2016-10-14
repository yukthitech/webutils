package com.yukthi.webutils.services;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * Utils to get default configuration with default directives. And also utility
 * method to parse freemarker templates.
 * 
 * @author akiran
 */
@Service
public class FreeMarkerService
{
	/**
	 * Singleton configuration.
	 */
	private Configuration configuration;

	/**
	 * Fetches default configuration.
	 * 
	 * @return default configuration.
	 */
	public Configuration getConfiguration()
	{
		if(configuration != null)
		{
			return configuration;
		}

		configuration = new Configuration();

		// add custom directive functions
		configuration.setSharedVariable("trim", (TemplateDirectiveModel) FreeMarkerService::trim);
		configuration.setSharedVariable("indent", (TemplateDirectiveModel) FreeMarkerService::indent);
		configuration.setSharedVariable("initcap", (TemplateDirectiveModel) FreeMarkerService::initcap);

		return configuration;
	}

	/**
	 * Utility method to process templates.
	 * 
	 * @param name
	 *            Name of the template, used for debugging.
	 * @param templateString
	 *            Template string to be processed.
	 * @param context
	 *            Context to be used for processing.
	 * @return Processed string.
	 */
	public String processTemplate(String name, String templateString, Object context)
	{
		try
		{
			Template template = new Template(name, templateString, getConfiguration());

			StringWriter writer = new StringWriter();
			template.process(context, writer);

			writer.flush();
			return writer.toString();
		} catch(Exception ex)
		{
			throw new IllegalStateException("An exception occurred while processing template: " + name, ex);
		}
	}

	/**
	 * Free marker directive method. Trims the output content inside this
	 * directive-tag
	 * 
	 * @param env
	 *            Environment under which processing is happening.
	 * @param params
	 *            Parameters of directive.
	 * @param loopVars
	 *            Template models.
	 * @param body
	 *            Body of the directive.
	 */
	@SuppressWarnings("rawtypes")
	private static void trim(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException
	{
		StringWriter writer = new StringWriter();
		body.render(writer);

		env.getOut().append(writer.toString().trim());
	}

	/**
	 * In the enclosing content, all lines gets trimmed, so that unwanted white
	 * spaces used for template formatting is removed. Later \t and \n are
	 * replaced with tab and new line feeds.
	 * 
	 * @param env
	 *            Environment under which processing is happening.
	 * @param params
	 *            Parameters of directive.
	 * @param loopVars
	 *            Template models.
	 * @param body
	 *            Body of the directive.
	 */
	@SuppressWarnings("rawtypes")
	private static void indent(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException
	{
		StringWriter writer = new StringWriter();
		body.render(writer);

		String actualOutput = writer.toString();
		Object prefix = params.get("prefix");

		if(prefix == null || !(prefix instanceof String))
		{
			prefix = "";
		}

		StringTokenizer st = new StringTokenizer(actualOutput, "\n");
		StringBuilder builder = new StringBuilder();
		String line = null;

		while(st.hasMoreTokens())
		{
			line = st.nextToken().trim();
			builder.append(prefix).append(line);
		}

		String output = builder.toString();
		output = output.replace("\\t", "\t");
		output = output.replace("\\n", "\n");

		env.getOut().append(output);
	}

	/**
	 * Converts each character of each word in the content to upper case and
	 * rest to lower case.
	 * 
	 * @param env
	 *            Environment under which processing is happening.
	 * @param params
	 *            Parameters of directive.
	 * @param loopVars
	 *            Template models.
	 * @param body
	 *            Body of the directive.
	 */
	@SuppressWarnings("rawtypes")
	private static void initcap(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException
	{
		StringWriter writer = new StringWriter();
		body.render(writer);

		String actualOutput = writer.toString();

		Pattern wordPattern = Pattern.compile("\\w+");
		Matcher matcher = wordPattern.matcher(actualOutput);
		String word = null;
		StringBuilder res = new StringBuilder();

		while(matcher.find())
		{
			word = matcher.group();
			word = word.toLowerCase();

			word = ("" + word.charAt(0)).toUpperCase() + word.substring(1);
		}

		env.getOut().append(res.toString());
	}
}
