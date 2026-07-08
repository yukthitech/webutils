package com.webutils.services.common;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.yukthitech.utils.fmarker.FreeMarkerEngine;
import com.yukthitech.utils.fmarker.annotaion.FreeMarkerDirective;
import com.yukthitech.utils.fmarker.annotaion.FreeMarkerMethod;

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
	
	public boolean evaluateCondition(String name, String templateString, Object context)
	{
		return freeMarkerEngine.evaluateCondition(name, templateString, context);
	}
	
	public Object fetchValue(String name, String expression, Object context)
	{
		return freeMarkerEngine.fetchValue(name, expression, context);
	}
}
