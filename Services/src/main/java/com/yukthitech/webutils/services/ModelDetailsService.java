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

import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.webutils.common.annotations.Model;
import com.yukthitech.webutils.common.lov.LovType;
import com.yukthitech.webutils.common.models.def.ModelDef;
import com.yukthitech.webutils.lov.LovRef;
import com.yukthitech.webutils.lov.LovService;
import com.yukthitech.webutils.lov.StoredLovService;
import com.yukthitech.webutils.services.def.ModelDefBuilder;

import jakarta.annotation.PostConstruct;

/**
 * Service for model details functionality.
 * 
 * @author akiran
 */
@Service
public class ModelDetailsService
{
	private static Logger logger = LogManager.getLogger(ModelDetailsService.class);
	
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

	/**
	 * Added as dependency to ensure all repositories are loaded before this class init is called . Note init() 
	 * loads model def which depends LOV service which in turn depends on repository-loader. Hence the autowiring is done
	 * to ensure it is loaded first 
	 */
	@SuppressWarnings("unused")
	@Autowired
	private WebutilsRepositoryFactory repositoryLoader;
	
	@Autowired
	private LovService lovService;
	
	@Autowired
	private StoredLovService storedLovService;
	
	/**
	 * Mapping from model name to java type.
	 */
	private Map<String, ModelDef> nameToModel = new HashMap<>();
	
	/**
	 * Maintains mapping from model type to def.
	 */
	private Map<Class<?>, ModelDef> typeToModel = new HashMap<>();
	
	private Set<LovRef> requiredLovs = new HashSet<>();
	
	/**
	 * Post construct method which scans for models and loads their definitions into map.
	 */
	@PostConstruct
	private void init()
	{
		Set<Class<?>> modelTypes = classScannerService.getClassesWithAnnotation(Model.class);
		ModelDef modelDef = null;
		
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
	}
	
	@EventListener
	public void postInitApp(ContextStartedEvent event)
	{
		for(LovRef lov : requiredLovs)
		{
			//ensure valid lov name is specified
			if(lov.getLovType() == LovType.DYNAMIC_TYPE && !lovService.isValidDynamicLov(lov.getName()))
			{
				throw new InvalidStateException("Invalid lov name '{}' specified on field {}", lov.getName(), lov.getFieldName());
			}

			if(lov.getLovType() == LovType.STORED_TYPE && !storedLovService.isValidLov(lov.getName()))
			{
				throw new InvalidStateException("Invalid lov name '{}' specified on field {}", lov.getName(), lov.getFieldName());
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
		return nameToModel.get(name);
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
