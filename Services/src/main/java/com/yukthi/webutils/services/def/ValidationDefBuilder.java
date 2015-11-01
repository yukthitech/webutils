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

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.yukthi.ccg.xml.XMLBeanParser;
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
	private Map<ValidationConfigKey, ValidationConfigDetails> configMap = new HashMap<>();
	
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
	 * @param config
	 */
	public void addValidationConfig(ValidationConfigDetails config)
	{
		configMap.put(new ValidationConfigKey(config.getType(), config.getTargetType(), true), config);
	}
	
	/**
	 * Used to fetch annotation attribute value specified by "name"
	 * @param annotation
	 * @param name
	 * @return
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
		
		//loop through annotations
		for(Annotation annotation: annotations)
		{
			resolverKey = new ValidationConfigKey(annotation.annotationType(), field.getType(), false);
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
			
			validationDef.setCrossValidation( annotation.annotationType().getAnnotation(CrossConstraint.class) != null );

			//TODO: Find a way to convert validation annotation default message expression into message
			validationDef.setErrorMessage((String)getAnnotationAttribute(annotation, "message"));
			
			validationDefLst.add(validationDef);
		}
		
		return validationDefLst;
	}
}
