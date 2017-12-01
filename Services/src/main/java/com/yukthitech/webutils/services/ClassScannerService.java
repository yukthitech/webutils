/*
 * The MIT License (MIT)
 * Copyright (c) 2015 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.yukthitech.webutils.services;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yukthitech.webutils.IWebUtilsInternalConstants;
import com.yukthitech.webutils.WebutilsConfiguration;

/**
 * Used to scan classes and find classes of specific type or with specified annotations. The scanning should be limited to certain packages.
 * The packages to be scanned should be specified using {@link WebutilsConfiguration#setBasePackages(List)}.
 * 
 * Reflection objects scans and indexes the class details. That is the reason they are loaded and cached during initialization (instead of creating them
 * on demand basis).
 * 
 * @author akiran
 */
@Service
public class ClassScannerService
{
	private static Logger logger = LogManager.getLogger(ClassScannerService.class);
	
	/**
	 * Autowired. Used to get the base packages of the webapplication which will be used
	 * for scanning.
	 */
	@Autowired
	private WebutilsConfiguration configuration;
	
	/**
	 * List of reflection objects created for each package
	 */
	private List<Reflections> reflections;

	/**
	 * Creates reflections object for all webapp packages specified in {@link #configuration} which in turn will be used for scanning
	 */
	@PostConstruct
	private void init()
	{
		List<String> packages = new ArrayList<>();
		
		//add yukthi package by default
		packages.add(IWebUtilsInternalConstants.WEBUTILS_BASE_PACKAGE);
		
		//add configured webapp base packages
		if(configuration.getBasePackages() != null)
		{
			packages.addAll(configuration.getBasePackages());
		}
		
		//create reflections for each package
		reflections = new ArrayList<Reflections>(packages.size());
		
		for(String pack: packages)
		{
			logger.debug("Enabling class scanning service for base package - '{}'", pack);
			reflections.add(new Reflections(pack, new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner()));
		}
	}

	/**
	 * Fetches all classes from all base packages with specified annotation.
	 * @param annotationType Target annotation for which scanning should be done
	 * @return List of classes having specified annotation
	 */
	public Set<Class<?>> getClassesWithAnnotation(Class<? extends Annotation> annotationType)
	{
		Set<Class<?>> result = new HashSet<Class<?>>();
		Set<Class<?>> classes = null;
		
		//loop through reflections of each package
		for(Reflections reflection: reflections)
		{
			classes = reflection.getTypesAnnotatedWith(annotationType);
			
			//if no classes found
			if(classes == null)
			{
				continue;
			}
			
			result.addAll(classes);
		}
		
		return result;
	}
	
	/**
	 * Fetches classes/interfaces of specified type and it's descendants.
	 * @param types Type of classes scanning is being done
	 * @return List of classes of specified type
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Set<Class<?>> getClassesOfType(Class<?>... types)
	{
		Set<Class<?>> result = new HashSet<>();
		Set<Class<?>> classes = null;
		
		for(Class<?> type : types)
		{
			//loop through reflection object of each base package
			for(Reflections reflection : reflections)
			{
				//find the classes for current base package
				classes = (Set) reflection.getSubTypesOf(type);
				
				if(classes == null)
				{
					continue;
				}
				
				//add to final result
				result.addAll(classes);
			}
		}
		
		return result;
	}

	/**
	 * Fetches methods with specified annotation.
	 * @param type Annotation type
	 * @return Set of methods having specified annotation
	 */
	public Set<Method> getMethodsAnnotatedWith(Class<? extends Annotation> type)
	{
		Set<Method> result = new HashSet<>();
		Set<Method> methods = null;
		
		//loop through reflection object of each base package
		for(Reflections reflection: reflections)
		{
			//find the methods for current base package
			methods = reflection.getMethodsAnnotatedWith(type);
			
			if(methods == null)
			{
				continue;
			}
			
			//add to final result
			result.addAll(methods);
		}
		
		return result;
	}
}
 