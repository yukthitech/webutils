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

package com.webutils.services.form.lov;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.webutils.common.ServiceMethod;
import com.webutils.common.form.annotations.Label;
import com.webutils.lov.LovOption;
import com.webutils.services.common.ClassScannerService;
import com.webutils.services.common.ConfgurationException;
import com.webutils.services.common.InvalidRequestException;
import com.webutils.services.common.SecurityService;
import com.yukthitech.utils.exceptions.InvalidConfigurationException;

/**
 * Service to fetch LOV values.
 * @author akiran
 */
@Service
public class LovService
{
	private static Logger logger = LogManager.getLogger(LovService.class);
	
	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private ClassScannerService classScannerService;
	
	@Autowired
	private SecurityService securityService;
	
	/**
	 * LOV method details cache.
	 */
	private Map<String, ServiceMethod> nameToLovMet = new HashMap<>();
	
	private boolean initialized = false;
	
	/**
	 * Lov list is loaded post app start, to ensure all repos are loaded before hand.
	 * @param event
	 */
	@EventListener
	public void postInitApp(ContextStartedEvent event)
	{
		if(initialized)
		{
			return;
		}
		
		initialized = true;
		
		loadLovMethods(LovMethod.class);
		loadLovMethods(LovQuery.class);
	}

	@SuppressWarnings("rawtypes")
	private void loadLovMethods(Class<? extends Annotation> annotType)
	{
		Set<Method> lovMethods = classScannerService.getMethodsAnnotatedWith(annotType);
		LovMethod lovMethod = null;
		String name = null;
		Object springComponent = null;
		
		//loop through all registered services
		for(Method method : lovMethods)
		{
			springComponent = applicationContext.getBean(method.getDeclaringClass());
			
			if(springComponent == null)
			{
				logger.info("Ignoring lov-method {}.{}() as the declaring class instance is not present in spring scope", method.getDeclaringClass().getName(), method.getName());
				continue;
			}
			
			if(method.getParameterCount() != 0)
			{
				throw new ConfgurationException("Lov-method {}.{}() is having parameters", method.getDeclaringClass().getName(), method.getName());
			}
			
			Type returnType = method.getGenericReturnType();
			
			if(!(returnType instanceof ParameterizedType))
			{
				throw new ConfgurationException("Lov-method {}.{}() is not having parameterized collection return type", method.getDeclaringClass().getName(), method.getName());
			}
			
			ParameterizedType parameterizedReturnType = (ParameterizedType) returnType;
			
			if(List.class.isAssignableFrom((Class) parameterizedReturnType.getRawType()))
			{
				throw new ConfgurationException("Lov-method {}.{}() is not having collection return type", method.getDeclaringClass().getName(), method.getName());
			}
			
			if(LovOption.class.isAssignableFrom((Class) parameterizedReturnType.getActualTypeArguments()[0]))
			{
				throw new ConfgurationException("Lov-method {}.{}() is not returning collection of LovOption.", method.getDeclaringClass().getName(), method.getName());
			}
			
			lovMethod = method.getAnnotation(LovMethod.class);
			name = lovMethod.name();
			
			ServiceMethod serviceMethod = new ServiceMethod(springComponent, method);
			
			if(nameToLovMet.containsKey(name))
			{
				throw new InvalidConfigurationException("Duplicate lov found with same name - {} [{}.{}(), {}.{}()]", 
						name,
						method.getDeclaringClass().getName(), method.getName(),
						serviceMethod.getMethod().getDeclaringClass().getName(), serviceMethod.getMethod().getName() 
						);
			}
			
			nameToLovMet.put(name, serviceMethod);
		}
	}

	/**
	 * Fetches specified enum fields as {@link ValueLabel} list.
	 * @param name Enum class name
	 * @return List of LOVs as {@link ValueLabel}
	 */
	@Cacheable("default")
	@SuppressWarnings("rawtypes")
	public List<LovOption> getEnumLovValues(String name)
	{
		try
		{
			Class<?> enumType = Class.forName(name);
			
			//if invalid enum is specified
			if(!enumType.isEnum())
			{
				throw new InvalidRequestException("Invalid enum type specified: " + name);
			}
			
			securityService.checkAuthorization(enumType);

			Object enumValues[] = enumType.getEnumConstants();
			List<LovOption> valueLst = new ArrayList<>();
			Enum<?> enumObj = null;
			String label = null;
			Label labelAnnot = null;
			Field field = null;
			
			//loop through enum fields
			for(Object obj : enumValues)
			{
				enumObj = (Enum)obj;
				
				//fetch enum field
				try
				{
					field = enumType.getField(enumObj.name());
				}catch(NoSuchFieldException | SecurityException e)
				{
					//ignore, this should never happen
					e.printStackTrace();
				}
				
				//Fetch the label for current enum field
				label = null;
				
				//if not found and @Label is defined on enum field
				if((labelAnnot = field.getAnnotation(Label.class)) != null)
				{
					//get @Label value
					label = labelAnnot.value();
				}
				
				//if label can not be found in any means
				if(label == null)
				{
					//use field name
					label = enumObj.name();
				}
				
				valueLst.add(new LovOption(enumObj.name(), label));
			}
			
			return valueLst;
		}catch(ClassNotFoundException ex)
		{
			throw new InvalidRequestException("Failed to fetch enum LOV for specified type: " + name, ex);
		}
	}
	
	/**
	 * Fetches dynamic LOV values based on the specified lov name. Before execution user authorization will be validated
	 * @param name Lov name
	 * @return dynamic LOV values based on the specified lov name
	 */
	@SuppressWarnings("unchecked")
	public List<LovOption> getDynamicLovValues(String name)
	{
		ServiceMethod serviceMethod = nameToLovMet.get(name);
		
		if(serviceMethod == null)
		{
			throw new InvalidParameterException("Invalid LOV name specified - " + name);
		}
		
		securityService.checkAuthorization(serviceMethod.getMethod());
		
		return (List<LovOption>) serviceMethod.invoke();
	}
	
	/**
	 * Checks if the specified name is valid dynamic lov name.
	 * @param name Name to be validated
	 * @return True, if specified name is valid dynamic lov name
	 */
	public boolean isValidDynamicLov(String name)
	{
		return nameToLovMet.containsKey(name);
	}

	public boolean isValidStaticLovValue(String name, String value)
	{
		return getEnumLovValues(name).stream().anyMatch(lov -> lov.getId().equals(value));
	}

	public boolean isValidDynamicLovValue(String name, String value)
	{
		return getDynamicLovValues(name).stream().anyMatch(lov -> lov.getId().equals(value));
	}
}
