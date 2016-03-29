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

package com.yukthi.webutils.controllers;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yukthi.utils.ObjectWrapper;
import com.yukthi.utils.exceptions.InvalidStateException;
import com.yukthi.webutils.InvalidRequestParameterException;
import com.yukthi.webutils.common.IExtendableModel;
import com.yukthi.webutils.common.annotations.ExtendableModel;
import com.yukthi.webutils.extensions.Extension;
import com.yukthi.webutils.extensions.ExtensionPointDetails;
import com.yukthi.webutils.repository.ExtensionEntity;
import com.yukthi.webutils.services.ExtensionService;

/**
 * Extension utils to fetch extension entity details.
 * @author akiran
 */
@Component
public class ExtensionUtil
{
	private static Logger logger = LogManager.getLogger(ExtensionUtil.class);
	
	@Autowired
	private ExtensionService extensionService;
	
	@Autowired(required = false)
	private IExtensionContextProvider extensionContextProvider;
	
	@Autowired
	private HttpServletRequest request;

	/**
	 * Fetches extension with specified name.
	 * @param extensionName Name of extension
	 * @param extensionWrapper empty Wrapper, if specified, corresponding extension will be set before returning. 
	 * @return Matching extension
	 */
	public ExtensionEntity getExtensionEntity(String extensionName, ObjectWrapper<Extension> extensionWrapper)
	{
		//if extension field is not specified by webapp, throw error
		if(extensionContextProvider == null)
		{
			throw new InvalidStateException("No extension helper is configured");
		}
		
		//fetch extension point details
		ExtensionPointDetails extensionPointDetails = extensionService.getExtensionPoint(extensionName);
		
		//if invalid extension name is specified
		if(extensionPointDetails == null)
		{
			throw new InvalidRequestParameterException("Invalid extension name specified - " + extensionName);
		}
		
		//fetch extension (with owner details) from helper
		Extension extension = extensionContextProvider.getExtension(extensionPointDetails, request);
		
		if(extension == null)
		{
			throw new InvalidStateException("Failed to fetch extension infromation");
		}
		
		if(extensionWrapper != null)
		{
			extensionWrapper.setValue(extension);
		}
		
		//fetch extension entity
		logger.debug("Trying to get extension - [Entity: {}, Owner Type: {}, Owner id: {}]", extensionPointDetails.getEntityType(), extension.getOwnerType(), extension.getOwnerId());
		ExtensionEntity extensionEntity = extensionService.getExtensionEntity(extensionPointDetails.getEntityType(), extension.getOwnerType(), extension.getOwnerId());
		
		if(extensionEntity == null)
		{
			return null;
		}
		
		return extensionEntity;
	}

	/**
	 * Gets extension entity for specified model target.
	 * @param target Model object for which extension needs to be fetched.
	 * @return Matching extension entity.
	 */
	public ExtensionEntity getExtensionEntity(Object target)
	{
		//when extension context if not provided, warn and ignore validation
		if(extensionContextProvider == null)
		{
			logger.warn("No extension context provider configured. Ignoring extensions- {}", target.getClass().getName());
			return null;
		}

		String extensionName = null;
		
		//ignore beans which are not marked as extendable models
		ExtendableModel extendableModelAnnotation = target.getClass().getAnnotation(ExtendableModel.class);
		
		//if no annotation found on model
		if(extendableModelAnnotation == null)
		{
			//but model is extendable model
			if(target instanceof IExtendableModel)
			{
				//try to get extension from context provider, to support dynamic extensions
				extensionName = extensionContextProvider.getExtensionName(target);
			}

			//if dynamic extension is also not available return null
			if(extensionName == null)
			{
				return null;
			}
		}
		//if annotation is present get extension name from annotation
		else
		{
			extensionName = extendableModelAnnotation.name();
		}
		
		if(!(target instanceof IExtendableModel))
		{
			throw new InvalidStateException("A non extendable-model (non IExtendableModel) is marked as @ExtendableModel - {}", target.getClass().getName());
		}

		//fetch extension entity for current context
		return getExtensionEntity(extensionName, null);
	}
}
