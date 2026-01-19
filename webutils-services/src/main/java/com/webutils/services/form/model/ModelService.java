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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.webutils.common.form.annotations.Model;
import com.webutils.common.form.model.LovType;
import com.webutils.common.form.model.ModelDef;
import com.webutils.services.common.ClassScannerService;
import com.webutils.services.common.InvalidRequestException;
import com.webutils.services.common.SecurityService;
import com.webutils.services.form.lov.LovRef;
import com.webutils.services.form.lov.LovService;
import com.webutils.services.form.lov.stored.StoredLovService;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Service for model details functionality.
 * 
 * @author akiran
 */
@Service
public class ModelService
{
	private static Logger logger = LogManager.getLogger(ModelService.class);
	
	/**
	 * Used to build model definition details.
	 */
	@Autowired
	private ModelDefBuilder modelDefBuilder;
	
	/**
	 * Scan services to scan model types.
	 */
	@Autowired
	private ClassScannerService classScannerService;

	@Autowired
	private LovService lovService;
	
	@Autowired
	private StoredLovService storedLovService;
	
	@Autowired
	private SecurityService securityService;
	
	/**
	 * Mapping from model name to java type.
	 */
	private Map<String, ModelDef> nameToModel = new HashMap<>();
	
	/**
	 * Maintains mapping from model type to def.
	 */
	private Map<Class<?>, ModelDef> typeToModel = new HashMap<>();
	
	/**
	 * scans for models and loads their definitions into map.
	 * Post init app is used, to ensure all repositories are loaded before this method.
	 */
	@EventListener
	public void postInitApp(ContextStartedEvent event)
	{
		// initialize lov service first, in case it is not initialized yet
		lovService.postInitApp(event);
		
		Set<Class<?>> modelTypes = classScannerService.getClassesWithAnnotation(Model.class);
		ModelDef modelDef = null;
		Set<LovRef> requiredLovs = new HashSet<>();
		
		//loop through model types and load their def
		for(Class<?> type : modelTypes)
		{
			//ignore classes without Model annotation
			if(type.getAnnotation(Model.class) == null)
			{
				continue;
			}
			
			logger.trace("Loading model type - " + type.getName());
			
			modelDef = modelDefBuilder.getModelDefinition(type, requiredLovs);
			
			if(nameToModel.containsKey(modelDef.getName()))
			{
				throw new InvalidStateException("Multiple models found with same name - {}", modelDef.getName());
			}
			
			nameToModel.put(modelDef.getName(), modelDef); 
			typeToModel.put(type, modelDef);
		}
		
		for(LovRef lov : requiredLovs)
		{
			//ensure valid lov name is specified
			if(lov.getLovType() == LovType.DYNAMIC_TYPE && !lovService.isValidDynamicLov(lov.getName()))
			{
				throw new InvalidStateException("Invalid lov name '{}' specified on field {}", lov.getName(), lov.getFieldName());
			}

			if(lov.getLovType() == LovType.STORED_TYPE && !storedLovService.isValidLov(lov.getName()))
			{
				throw new InvalidStateException("Non-existing stored-lov name '{}' specified on field {}", lov.getName(), lov.getFieldName());
			}
		}		
	}
	
	/**
	 * Used to fetch model definitions for specified model name. 
	 * @param name of the model whose definition needs to be fetched
	 * @return model definitions for specified model name. 
	 */
	public ModelDef getModelDef(String name)
	{
		ModelDef res = nameToModel.get(name);
		
		if(res == null)
		{
			throw new InvalidRequestException("No model def found with specified name: {}", name);
		}
		
		securityService.checkAuthorization(res.getClazz());
		
		return res;
	}
	
	/**
	 * Fetches model definition based on the type.
	 * @param type Type of model
	 * @return Matching model definition
	 */
	public ModelDef getModelDef(Class<?> type)
	{
		return typeToModel.get(type);
	}
}
