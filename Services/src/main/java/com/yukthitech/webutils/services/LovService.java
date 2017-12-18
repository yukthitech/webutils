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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.utils.exceptions.InvalidConfigurationException;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.webutils.IRepositoryMethodRegistry;
import com.yukthitech.webutils.IWebUtilsInternalConstants;
import com.yukthitech.webutils.InvalidRequestParameterException;
import com.yukthitech.webutils.WebutilsContext;
import com.yukthitech.webutils.annotations.LovMethod;
import com.yukthitech.webutils.annotations.LovQuery;
import com.yukthitech.webutils.common.annotations.Label;
import com.yukthitech.webutils.common.models.ValueLabel;
import com.yukthitech.webutils.security.ISecurityService;
import com.yukthitech.webutils.security.SecurityInvocationContext;
import com.yukthitech.webutils.security.UnauthorizedException;
import com.yukthitech.webutils.security.WebutilsSecurityService;
import com.yukthitech.webutils.services.dynamic.DynamicMethod;
import com.yukthitech.webutils.utils.WebUtils;


/**
 * Service to fetch LOV values.
 * @author akiran
 */
@Service
public class LovService implements IRepositoryMethodRegistry<LovQuery>
{
	private static Logger logger = LogManager.getLogger(LovService.class);
	
	/**
	 * Message source to fetch ENUM field labels.
	 */
	@Autowired
	private MessageSource messageSource;
	
	/**
	 * Security service to check authorization of target method.
	 */
	@Autowired(required = false)
	private ISecurityService securityService;
	
	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private ClassScannerService classScannerService;

	/**
	 * LOV method details cache.
	 */
	private Map<String, DynamicMethod> nameToLovMet = new HashMap<>();
	
	/**
	 * Security service.
	 */
	@Lazy
	@Autowired
	private WebutilsSecurityService webutilsSecurityService;

	@PostConstruct
	private void init()
	{
		Set<Method> lovMethods = classScannerService.getMethodsAnnotatedWith(LovMethod.class);
		LovMethod lovMethod = null;
		DynamicMethod dynamicMethod = null;
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
			
			lovMethod = method.getAnnotation(LovMethod.class);
				
			if(method.getParameterTypes().length > 0)
			{
				throw new InvalidConfigurationException("Non zero parameter method is declared as LOV method - {}.{}()", method.getDeclaringClass().getName(), method.getName());
			}
				
			name = lovMethod.name();
			
			dynamicMethod = new DynamicMethod(method.getDeclaringClass(), method, null);
			dynamicMethod.setDefaultObject(springComponent);
			
			if(nameToLovMet.containsKey(name))
			{
				throw new InvalidConfigurationException("Duplicate lov found with same name - {}, {}", nameToLovMet.get(name), dynamicMethod);
			}
			
			nameToLovMet.put(name, dynamicMethod);
		}
	}
	
	@Override
	public void registerRepositoryMethod(Method method, LovQuery annotation, ICrudRepository<?> repository)
	{
		throw new InvalidStateException("This method is not expected to be invoked");
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void registerDynamicMethod(DynamicMethod method, LovQuery annotation)
	{
		Type returnType = method.getMethod().getGenericReturnType();
		
		if(!(returnType instanceof ParameterizedType))
		{
			throw new IllegalStateException("Invalid return type specified for lov method - " + method);
		}
		
		ParameterizedType parameterizedType = (ParameterizedType)returnType;
		
		if( !Collection.class.isAssignableFrom((Class)parameterizedType.getRawType()) || 
				!ValueLabel.class.equals(parameterizedType.getActualTypeArguments()[0]) )
		{
			throw new IllegalStateException("Invalid return type specified for lov method - " + method);
		}

		String lovName = annotation.name();
		
		//if duplicate lov name is encountered throw error
		if(nameToLovMet.containsKey(lovName))
		{
			throw new InvalidConfigurationException("Duplicate LOV configuration encountered. Same name '{}' is used by two LOV methods - {}, {}", 
					WebUtils.toString( nameToLovMet.get(lovName).getMethod() ), 
					WebUtils.toString( method.getMethod() ) 
			);
		}
		
		logger.info("Loading lov method - {}.{}", method.getMethod().getDeclaringClass().getName(), method.getMethod().getName());
		nameToLovMet.put(lovName, method);
	}

	/**
	 * Fetches specified enum fields as {@link ValueLabel} list.
	 * @param name Enum class name
	 * @param locale Local in which LOV needs to be fetched
	 * @return List of LOVs as {@link ValueLabel}
	 */
	@Cacheable("default")
	@SuppressWarnings("rawtypes")
	public List<ValueLabel> getEnumLovValues(String name, Locale locale)
	{
		try
		{
			Class<?> enumType = Class.forName(name);
			
			//if invalid enum is specified
			if(!enumType.isEnum())
			{
				throw new InvalidRequestParameterException("Invalid enum type specified: " + name);
			}

			Object enumValues[] = enumType.getEnumConstants();
			List<ValueLabel> valueLst = new ArrayList<>();
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
				
				//try to fetch from message source using <enum-name>.<field-name>.label
				label = getMessage(enumType.getName() + "." + enumObj.name() + ".label", locale);
				
				//if not found
				if(label == null)
				{
					//try to fetch from message source using <field-name>.label
					label = getMessage(enumObj.name() + ".label", locale);
				}
				
				//if not found and @Label is defined on enum field
				if(label == null && (labelAnnot = field.getAnnotation(Label.class)) != null)
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
				
				valueLst.add(new ValueLabel(enumObj.name(), label));
			}
			
			//Note: By default enums are ordered in order of definition
			/*
			//sort the lov list
			Collections.sort(valueLst, new Comparator<ValueLabel>()
			{
				@Override
				public int compare(ValueLabel o1, ValueLabel o2)
				{
					return o1.getLabel().compareTo(o2.getLabel());
				}
			});
			*/
			
			return valueLst;
		}catch(ClassNotFoundException ex)
		{
			throw new InvalidRequestParameterException("Failed to fetch enum LOV for specified type: " + name, ex);
		}
	}
	
	/**
	 * Tries to fetch message with specified key, if not found returns null
	 * @param key Key for which message needs to be fetched
	 * @param locale
	 * @return Message matching with the key
	 */
	private String getMessage(String key, Locale locale)
	{
		try
		{
			return messageSource.getMessage(key, null, locale);
		}catch(Exception ex)
		{
			return null;
		}
	}
	
	/**
	 * Fetches dynamic LOV values based on the specified lov name. Before execution user authorization will be validated
	 * @param name Lov name
	 * @param dependencyValue If LOV is dependent on other field, that dependency field value should be specified here
	 * @param locale Locale in which values needs to be fetched. Current this is not used
	 * @return dynamic LOV values based on the specified lov name
	 */
	@SuppressWarnings("unchecked")
	public List<ValueLabel> getDynamicLovValues(String name, String dependencyValue, Locale locale)
	{
		DynamicMethod method = nameToLovMet.get(name);
		
		if(method == null)
		{
			throw new InvalidParameterException("Invalid LOV name specified - " + name);
		}
		
		//if security service is specified, check user authorization for target search method
		if(securityService != null)
		{
			SecurityInvocationContext context = webutilsSecurityService.newSecurityInvocationContext(method.getType(), method.getMethod());
			
			if(!securityService.isAuthorized(context))
			{
				throw new UnauthorizedException("Current user is not authorized to execute lov query - {}", name);
			}
		}

		WebutilsContext.getContext().addAttribute(IWebUtilsInternalConstants.CONTEXT_ATTR_LOV_DEPENDENCY_VAL, dependencyValue);
		return (List<ValueLabel>) method.invoke();
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
}
