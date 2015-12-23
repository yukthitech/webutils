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

package com.yukthi.webutils.services.def;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.yukthi.ccg.xml.XMLBeanParser;
import com.yukthi.utils.CommonUtils;
import com.yukthi.utils.exceptions.InvalidStateException;
import com.yukthi.validation.cross.CrossConstraint;
import com.yukthi.webutils.common.models.def.ValidationDef;

/**
 * @author akiran
 *
 */
@Component
public class ValidationDefBuilder
{
	private static final Pattern EXPR_PATTERN = Pattern.compile("\\$?\\{([\\w\\.]+)\\}");
	
	private Map<ValidationConfigKey, ValidationConfigDetails> configMap = new HashMap<>();
	
	@Autowired
	@Qualifier("validatorMessageSource")
	private MessageSource validationMessageSource;
	
	/**
	 * Post construct method used to load validation configuration xml file
	 */
	@PostConstruct
	private void init()
	{
		XMLBeanParser.parse(ValidationDefBuilder.class.getResourceAsStream("validation-mapping.xml"), this);
	}
	
	/**
	 * Invoked by xml bean parser, while parsing xml, to add each validation configuration
	 * @param config Configuration to be added
	 */
	public void addValidationConfig(ValidationConfigDetails config)
	{
		configMap.put(new ValidationConfigKey(config.getType(), config.getTargetType(), true), config);
	}
	
	/**
	 * Used to fetch annotation attribute value specified by "name"
	 * @param annotation Annotation from which attribute needs to be fetched
	 * @param name Name of attribute whose value needs to be fetched
	 * @return Specified attribute value
	 */
	private Object getAnnotationAttribute(Annotation annotation, String name)
	{
		try
		{
			Class<?> annotationClass = annotation.getClass();
			Method method = annotationClass.getDeclaredMethod(name);
			return method.invoke(annotation);
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while fetching attribute '{}' from annotation", name, annotation.annotationType().getName());
		}
	}
	
	/**
	 * Fetches the error message for the specified validation annotation, which should be used
	 * when validation fails. If the message is matching with pattern - {message}, then the value for the message
	 * will be pulled from resource bundle. Similarly, {field} like expressions can be used in message (from bundle or within annotation)
	 * to refer to annotation fields. ${key} like expression will be retained, which can be used by client to place runtime values.
	 * @param annotation
	 * @return
	 */
	protected String getMessage(Annotation annotation)
	{
		String message = (String)getAnnotationAttribute(annotation, "message");
		Matcher matcher = EXPR_PATTERN.matcher(message);

		//if message itself is expression, get message template from resource bundle
		if(matcher.matches())
		{
			try
			{
				message = validationMessageSource.getMessage(matcher.group(1), null, null);
			}catch(Exception ex)
			{
				throw new InvalidStateException("Failed to find message for annotation {} with message key - {}", annotation.annotationType().getName(), matcher.group(1));
			}
		}
		
		//rebuild the matcher, this time look for patterns with in the message
		matcher = EXPR_PATTERN.matcher(message);
	
		StringBuffer finalMsg = new StringBuffer();
		String value = null;
		
		//replace expressions with annotation attributes
		while(matcher.find())
		{
			if(matcher.group(0).startsWith("$"))
			{
				value = "\\" + matcher.group(0);
			}
			else
			{
				value = "" + getAnnotationAttribute(annotation, matcher.group(1));
			}
			
			matcher.appendReplacement(finalMsg, value);
		}
		
		matcher.appendTail(finalMsg);
		return finalMsg.toString();
	}

	/**
	 * Get validations for specified model type and field
	 * @param modelType Model type whose field validations needs to be fetched
	 * @param field Field for which validations needs to be fetched
	 * @return Specified field validations
	 */
	public Collection<ValidationDef> getValidations(Class<?> modelType, Field field)
	{
		Annotation annotations[] = field.getAnnotations();
		
		//if no annotations are defined, return empty list
		if(annotations == null || annotations.length == 0)
		{
			return Collections.emptyList();
		}
		
		ValidationConfigKey resolverKey = null;
		ValidationConfigDetails resolverDetails = null;
		
		List<ValidationDef> validationDefLst = new ArrayList<>();
		ValidationDef validationDef = null;
		Object value = null;
		Map<String, Object> valueMap = new HashMap<>();
		Class<?> fieldType = field.getType();
		
		//if field type id primitive, convert type to corresponding wrapper type
		if(fieldType.isPrimitive())
		{
			fieldType = CommonUtils.getWrapperType(fieldType);
			
			if(fieldType == null)
			{
				throw new IllegalStateException("Failed to find wrapper of primitive type - " + field.getType().getName());
			}
		}
		
		//loop through annotations
		for(Annotation annotation: annotations)
		{
			resolverKey = new ValidationConfigKey(annotation.annotationType(), fieldType, false);
			resolverDetails = configMap.get(resolverKey);
			
			//if current annotation is not found to be supported validation annotation, ignore
			if(resolverDetails == null)
			{
				continue;
			}

			//clear annotation value map
			valueMap.clear();
			
			//create new validation def and set values
			validationDef = new ValidationDef();
			validationDef.setName(resolverDetails.getName());
			
			//loop through required annotation attribute names and get values from annotations
			for(String attr: resolverDetails.getValueAttrs())
			{
				value = getAnnotationAttribute(annotation, attr);
			
				valueMap.put(attr, value);
			}
			
			//if validation has attribute values
			if(!valueMap.isEmpty())
			{
				validationDef.setValues(new HashMap<>(valueMap));
			}
			
			//set flag to indicate if the validation is cross field validation
			validationDef.setCrossValidation( annotation.annotationType().getAnnotation(CrossConstraint.class) != null );

			//set the error message to be used by client when validation fails
			validationDef.setErrorMessage(getMessage(annotation));
			
			validationDefLst.add(validationDef);
		}
		
		return validationDefLst;
	}
}
