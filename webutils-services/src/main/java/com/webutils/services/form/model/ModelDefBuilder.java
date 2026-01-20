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
package com.webutils.services.form.model;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.webutils.common.form.annotations.IgnoreField;
import com.webutils.common.form.annotations.Model;
import com.webutils.common.form.model.FieldDef;
import com.webutils.common.form.model.ModelDef;
import com.webutils.services.form.lov.LovRef;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Factory for generating model def based on specified java type
 * @author akiran
 */
@Component
public class ModelDefBuilder
{
	/**
	 * Field def builder
	 */
	@Autowired
	private FieldDefBuilder fieldDefBuilder;
	
	@Value("${app.date.format:dd/MM/yyyy}")
	private String dateFormat;
	
	@Value("${app.date.jsDateFormat:DD/MM/YYYY}")
	private String jsDateFormat;

	/**
	 * Fetches model name of specified type
	 * @param beanType
	 * @return Model name
	 */
	private String getModelName(Class<?> beanType)
	{
		Model model = beanType.getAnnotation(Model.class);
		
		//if model annotation is not found
		if(model == null)
		{
			throw new InvalidStateException("Trying to load bean type {} as model, but class is not annotated with @Model", beanType.getName());
		}
		
		String modelName = StringUtils.isBlank(model.name()) ? beanType.getSimpleName() : model.name();
		return modelName;
	}

	/**
	 * Builds and returns model definition for specified modelType.
	 * @param modelType Type for which def needs to be built
	 * @param requiredLovs Used to collect required lovs by this model
	 * @return Model def representing specified type
	 */
	public ModelDef getModelDefinition(Class<?> modelType, Set<LovRef> requiredLovs)
	{
		String modelName = getModelName(modelType);
		
		//create model def instance and set basic properties
		ModelDef modelDef = new ModelDef(modelType);
		modelDef.setName(modelName);
		
		
		modelDef.setLabel(DefUtils.getLabel(modelType, modelType.getSimpleName()));
		
		//fetch field definitions and set it on model type def
		List<FieldDef> fieldDefLst = new ArrayList<>();
		Class<?> curCls = modelType;
		
		while(true)
		{
			if(curCls.getName().startsWith("java"))
			{
				break;
			}
			
			Field fields[] = curCls.getDeclaredFields();
			
			for(Field field : fields)
			{
				//ignore static fields
				if(Modifier.isStatic(field.getModifiers()))
				{
					continue;
				}
				
				//if field is marked to be ignore, ignore
				if(field.getAnnotation(IgnoreField.class) != null)
				{
					continue;
				}
				
				fieldDefLst.add(fieldDefBuilder.getFieldDef(modelType, field, requiredLovs));
			}
			
			curCls = curCls.getSuperclass();
		}
		
		modelDef.setFields(fieldDefLst);
		modelDef.setDateFormat(dateFormat);
		modelDef.setJsDateFormat(jsDateFormat);
		
		return modelDef;
	}
}
