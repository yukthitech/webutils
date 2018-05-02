package com.yukthitech.webutils.services.freemarker;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yukthitech.utils.ConvertUtils;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.webutils.services.ClassScannerService;

import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

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
	 * Model wrapper for free marker dynamic method registration.
	 * @author akiran
	 */
	private static class FreeMarkerMethodModel implements TemplateMethodModelEx
	{
		/**
		 * Method being registered.
		 */
		private Method freeMarkerMethod;
		
		/**
		 * Method name that will be used in free marker templates.
		 */
		private String methodName;
		
		/**
		 * Instantiates a new free marker method model.
		 *
		 * @param freeMarkerMethod the free marker method
		 * @param methodName the method name
		 */
		public FreeMarkerMethodModel(Method freeMarkerMethod, String methodName)
		{
			this.freeMarkerMethod = freeMarkerMethod;
			this.methodName = methodName;
		}

		/* (non-Javadoc)
		 * @see freemarker.template.TemplateMethodModelEx#exec(java.util.List)
		 */
		@SuppressWarnings("rawtypes")
		@Override
		public Object exec(List arguments) throws TemplateModelException
		{
			Class<?> argTypes[] = freeMarkerMethod.getParameterTypes();
			boolean isVarArgs = freeMarkerMethod.isVarArgs();
			Object methodArgs[] = null;
			int argsSize = arguments != null ? arguments.size() : 0;
			
			if(!isVarArgs)
			{
				if(argsSize != argTypes.length)
				{
					throw new InvalidArgumentException("Invalid number of arguments specified for method - {}", methodName);
				}

				//for normal arguments, number of method arguments will be equal to actual arguments
				methodArgs = new Object[arguments != null ? arguments.size() : 0];
			}
			else
			{
				if(argsSize < argTypes.length - 1)
				{
					throw new InvalidArgumentException("Invalid number of arguments specified for method - {}", methodName);
				}
				
				//for var args number of arguments will be equal to number of declared parameters in method
				// 	last + extra param will be clubbed into single array for varargs
				methodArgs = new Object[argTypes.length];
			}
			
			if(argsSize > 0)
			{
				int stdArgCount = isVarArgs ? argTypes.length - 1 : argTypes.length;
				
				for(int i = 0; i < stdArgCount; i++)
				{
					methodArgs[i] = ConvertUtils.convert(arguments.get(i), argTypes[i]);
				}
				
				if(isVarArgs && argsSize >= argTypes.length)
				{
					Class<?> varArgType = argTypes[argTypes.length - 1].getComponentType();
					Object varArgs = Array.newInstance(varArgType, argsSize - stdArgCount); 
					
					for(int i = stdArgCount, j = 0; i < argsSize; i++, j++)
					{
						Array.set( varArgs, j, ConvertUtils.convert(arguments.get(i), varArgType) );
					}
					
					methodArgs[stdArgCount] = varArgs;
				}
			}

			try
			{
				return freeMarkerMethod.invoke(null, methodArgs);
			}catch(Exception ex)
			{
				throw new TemplateModelException(ex);
			}
		}
	}
	
	/**
	 * Scanner service to scan for free marker methods.
	 */
	@Autowired
	private ClassScannerService classScannerService;
	
	/**
	 * Singleton configuration.
	 */
	private Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
	
	/**
	 * Registry of free marker methods.
	 */
	private Map<String, Method> freeMarkerMethodRegistry = new HashMap<>();

	/**
	 * Post construct method to initialize default configuration.
	 */
	@PostConstruct
	private void init()
	{
		// add custom directive functions
		configuration.setSharedVariable("trim", (TemplateDirectiveModel) FreeMarkerService::trim);
		configuration.setSharedVariable("indent", (TemplateDirectiveModel) FreeMarkerService::indent);
		configuration.setSharedVariable("initcap", (TemplateDirectiveModel) FreeMarkerService::initcap);
		
		Set<Method> freeMarkerMethods = classScannerService.getMethodsAnnotatedWith(FreemarkerMethod.class);
		
		if(freeMarkerMethods == null)
		{
			return;
		}
		
		for(Method method : freeMarkerMethods)
		{
			registerMethod(method);
		}
	}
	
	/**
	 * Registers specified method into registry.
	 * Made into default for test cases usage.
	 * @param method Method to register.
	 */
	void registerMethod(Method method)
	{
		if(!Modifier.isStatic(method.getModifiers()) || !Modifier.isPublic(method.getModifiers()))
		{
			throw new InvalidStateException("A non-public-static Method {}.{}() is marked as freemarker-method", method.getDeclaringClass().getName(), method.getName());
		}
		
		if(void.class.equals(method.getReturnType()))
		{
			throw new InvalidStateException("A void method {}.{}() is marked as freemarker-method", method.getDeclaringClass().getName(), method.getName());
		}
		
		FreemarkerMethod freemarkerMethodAnnot = method.getAnnotation(FreemarkerMethod.class);
		String name = freemarkerMethodAnnot.value().trim();
		
		if(StringUtils.isBlank(name))
		{
			name = method.getName();
		}
		
		if(freeMarkerMethodRegistry.containsKey(name))
		{
			Method duplicateMethod = freeMarkerMethodRegistry.get(name);
			
			throw new InvalidStateException("Multiple free marker methods are found with same name - [{}.{}(), {}.{}()]", 
					duplicateMethod.getDeclaringClass().getName(), duplicateMethod.getName(),
					method.getDeclaringClass().getName(), method.getName());
		}
		
		configuration.setSharedVariable(name, new FreeMarkerMethodModel(method, name));
		freeMarkerMethodRegistry.put(name, method);
	}

	/**
	 * Fetches default configuration.
	 * 
	 * @return default configuration.
	 */
	public Configuration getConfiguration()
	{
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
		if(templateString == null)
		{
			return null;
		}
		
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
	 * Processes specified template string as condition. If condition results in true, true will be returned otherwise false.
	 * @param name name of template
	 * @param templateString condition template string
	 * @param context context to be used
	 * @return true if condition evaluates to true.
	 */
	public boolean processConditionTemplate(String name, String templateString, Object context)
	{
		String conditionTemplate = String.format("<#if %s>true<#else>false</#if>", templateString);
		String res = processTemplate(name, conditionTemplate, context);
		
		return "true".equalsIgnoreCase(res);
	}
	
	/**
	 * Process the method condition by create context with following key value pairs:
	 * 		target - target object on which method is going to be invoked
	 * 		method - method being invoked
	 * 		parameters - Array of objects being passed as params to method
	 * 		a0,a1,.. an - Easy way of accessing arguments with 'a' prefix
	 * 		p0,p1,.. pn - Easy way of accessing arguments with 'p' prefix
	 * 		other parameters specified in extra context params if specified.
	 * 		
	 * @param conditionTemplate template to process
	 * @param method method being invoked.
	 * @param target target on which method is being invoked
	 * @param args arguments being passed to method invocation
	 * @param extraContextParams custom extra params
	 * @return true if condition is evaluated to true. Otherwise false.
	 */
	public boolean processMethodCondition(String conditionTemplate, Method method, Object target, Object args[], Map<String, Object> extraContextParams)
	{
		Map<String, Object> context = new HashMap<>();
		
		context.put("target", target);
		context.put("method", method);
		
		context.put("parameters", args);
		
		if(args != null)
		{
			for(int i = 0; i < args.length; i++)
			{
				context.put("a" + i, args[i]);
				context.put("p" + i, args[i]);
			}
		}
		
		if(extraContextParams != null)
		{
			context.putAll(extraContextParams);
		}
		
		return processConditionTemplate(method.getName(), conditionTemplate, context);
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
