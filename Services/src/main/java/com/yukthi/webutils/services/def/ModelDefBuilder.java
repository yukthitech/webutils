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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yukthi.utils.exceptions.InvalidStateException;
import com.yukthi.webutils.WebutilsConfiguration;
import com.yukthi.webutils.common.IExtendableModel;
import com.yukthi.webutils.common.annotations.ExtendableModel;
import com.yukthi.webutils.common.annotations.IgnoreField;
import com.yukthi.webutils.common.annotations.Model;
import com.yukthi.webutils.common.models.def.FieldDef;
import com.yukthi.webutils.common.models.def.ModelDef;

/**
 * Factory for generating model def based on specified java type
 * @author akiran
 */
@Component
public class ModelDefBuilder
{
	/**
	 * Common def utils
	 */
	@Autowired
	private DefUtils defUtils;
	
	/**
	 * Field def builder
	 */
	@Autowired
	private FieldDefBuilder fieldDefBuilder;
	
	@Autowired
	private WebutilsConfiguration configuration;
	
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
	 * Builds and returns model definition for specified modelType
	 * @param modelType Type for which def needs to be built
	 * @return Model def representing specified type
	 */
	public ModelDef getModelDefinition(Class<?> modelType)
	{
		String modelName = getModelName(modelType);
		
		//create model def instance and set basic properties
		ModelDef modelDef = new ModelDef();
		modelDef.setName(modelName);
		modelDef.setLabel(defUtils.getLabel(modelType, modelType.getSimpleName(), modelType.getName()));
		
		ExtendableModel extendableModelAnnot = modelType.getAnnotation(ExtendableModel.class);
		
		if(extendableModelAnnot != null && IExtendableModel.class.isAssignableFrom(modelType))
		{
			modelDef.setExtensionName(extendableModelAnnot.name());
		}
		
		//fetch field definitions and set it on model type def
		List<FieldDef> fieldDefLst = new ArrayList<>();
		Field fields[] = modelType.getDeclaredFields();
		
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
			
			fieldDefLst.add(fieldDefBuilder.getFieldDef(modelType, field));
		}
		
		modelDef.setFields(fieldDefLst);
		modelDef.setDateFormat(configuration.getDateFormat().toPattern());
		
		return modelDef;
	}
}
