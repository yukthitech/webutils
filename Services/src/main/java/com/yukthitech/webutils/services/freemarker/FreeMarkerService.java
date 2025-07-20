package com.yukthitech.webutils.services.freemarker;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.yukthitech.utils.fmarker.FreeMarkerEngine;
import com.yukthitech.utils.fmarker.annotaion.FreeMarkerDirective;
import com.yukthitech.utils.fmarker.annotaion.FreeMarkerMethod;
import com.yukthitech.webutils.services.ClassScannerService;

import jakarta.annotation.PostConstruct;

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
	 * Scanner service to scan for free marker methods.
	 */
	@Autowired
	private ClassScannerService classScannerService;
	
	@Value("${webutils.fmarker.excludeDefaultDirectives:false}")
	private boolean excludeDefaultDirectives;
	
	@Value("${webutils.fmarker.excludeDefaultMehods:false}")
	private boolean excludeDefaultMehods;

	private FreeMarkerEngine freeMarkerEngine;
	
	/**
	 * Post construct method to initialize default configuration.
	 */
	@PostConstruct
	private void init()
	{
		freeMarkerEngine = new FreeMarkerEngine(excludeDefaultMehods, excludeDefaultDirectives);
		
		Set<Method> freeMarkerMethods = classScannerService.getMethodsAnnotatedWith(FreeMarkerMethod.class);
		registerMethods(freeMarkerMethods);
		
		Set<Method> dirMethods = classScannerService.getMethodsAnnotatedWith(FreeMarkerDirective.class);
		registerMethods(dirMethods);
	}
	
	private void registerMethods(Set<Method> freeMarkerMethods)
	{
		if(freeMarkerMethods == null)
		{
			return;
		}
		
		Set<Class<?>> fmarkerClasses = new HashSet<Class<?>>();
		
		for(Method method : freeMarkerMethods)
		{
			if(fmarkerClasses.contains(method.getDeclaringClass()))
			{
				continue;
			}
		
			freeMarkerEngine.loadClass(method.getDeclaringClass());
		}
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
		return freeMarkerEngine.processTemplate(name, templateString, context);
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
}
