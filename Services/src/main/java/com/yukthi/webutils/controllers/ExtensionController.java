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

import static com.yukthi.webutils.common.IWebUtilsActionConstants.ACTION_PREFIX_EXTENSIONS;
import static com.yukthi.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_DELETE;
import static com.yukthi.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_DELETE_ALL;
import static com.yukthi.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_FETCH;
import static com.yukthi.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_SAVE;
import static com.yukthi.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_UPDATE;
import static com.yukthi.webutils.common.IWebUtilsActionConstants.PARAM_ID;
import static com.yukthi.webutils.common.IWebUtilsActionConstants.PARAM_NAME;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.yukthi.persistence.annotations.DataType;
import com.yukthi.persistence.conversion.impl.JsonConverter;
import com.yukthi.utils.ObjectWrapper;
import com.yukthi.utils.exceptions.InvalidStateException;
import com.yukthi.webutils.InvalidRequestParameterException;
import com.yukthi.webutils.annotations.ActionName;
import com.yukthi.webutils.common.extensions.ExtensionFieldType;
import com.yukthi.webutils.common.models.BaseResponse;
import com.yukthi.webutils.common.models.BasicSaveResponse;
import com.yukthi.webutils.common.models.ExtensionFieldModel;
import com.yukthi.webutils.common.models.ExtensionFieldsResponse;
import com.yukthi.webutils.extensions.Extension;
import com.yukthi.webutils.extensions.ExtensionPointDetails;
import com.yukthi.webutils.repository.ExtensionEntity;
import com.yukthi.webutils.repository.ExtensionFieldEntity;
import com.yukthi.webutils.services.ExtensionService;
import com.yukthi.webutils.utils.WebUtils;

/**
 * Controller for fetching LOV values.
 * @author akiran
 */
@RestController
@ActionName(ACTION_PREFIX_EXTENSIONS)
@RequestMapping("/extensions")
public class ExtensionController extends BaseController
{
	private static Logger logger = LogManager.getLogger(ExtensionController.class);
	
	@Autowired
	private ExtensionService extensionService;
	
	@Autowired
	private ExtensionUtil extensionUtil;
	
	private JsonConverter jsonConverter = new JsonConverter();
	
	/**
	 * Fetches extension fields for specified extension name (for current or request specific owner)
	 * @param extensionName Name of the entity extension
	 * @param request Http request
	 * @return Response holding extension fields
	 */
	@ActionName(ACTION_TYPE_FETCH)
	@ResponseBody
	@RequestMapping(value = "/fetch/{" + PARAM_NAME + "}", method = RequestMethod.POST)
	public ExtensionFieldsResponse fetchExtensionFields(@PathVariable(PARAM_NAME) String extensionName, HttpServletRequest request)
	{
		logger.trace("Fetching extension fields for - {}", extensionName);
		
		ExtensionEntity extensionEntity = extensionUtil.getExtensionEntity(extensionName, null);
		
		if(extensionEntity == null)
		{
			logger.debug("No extension entity found for extension - {}", extensionName);
			return new ExtensionFieldsResponse(Collections.emptyList());
		}
		
		//fetch extension fields and build response
		List<ExtensionFieldEntity> extensionFields = extensionService.getExtensionFields(extensionEntity.getId());
		logger.debug("Found {} extension fields", (extensionFields != null)? extensionFields.size() : 0);
		
		List<ExtensionFieldModel> extensionFieldModels = WebUtils.convertBeans(extensionFields, ExtensionFieldModel.class);
		
		return new ExtensionFieldsResponse(extensionFieldModels);
	}

	/**
	 * Validates specified extension field model. Throws exception if validation fails
	 * @param extensionField
	 */
	private void validateExtensionField(ExtensionFieldModel extensionField)
	{
		if(extensionField.getType() == ExtensionFieldType.LIST_OF_VALUES)
		{
			if(CollectionUtils.isEmpty(extensionField.getLovOptions()))
			{
				logger.error("No LOV options specified for lov field");
				throw new InvalidRequestParameterException("No LOV options specified for LOV field");
			}
			
			String lovOptStr = (String)jsonConverter.convertToDBType(extensionField.getLovOptions(), DataType.STRING);
			
			if(lovOptStr.length() > 2000)
			{
				logger.error("Too many or too long lov options specified. Got result json string length as - ", lovOptStr.length());
				throw new InvalidRequestParameterException("Too many or too long LOV options specified");
			}
		}
	}
	
	/**
	 * Saves the extension field with specified name. If no extension exists, a new extension gets created
	 * @param extensionName Name of the extension
	 * @param extensionField Extension field model
	 * @param request Http Request
	 * @return Save response with id
	 */
	@ActionName(ACTION_TYPE_SAVE)
	@ResponseBody
	@RequestMapping(value = "/save/{" + PARAM_NAME + "}", method = RequestMethod.POST)
	public BasicSaveResponse saveExtensionField(@PathVariable(PARAM_NAME) String extensionName, @RequestBody @Valid ExtensionFieldModel extensionField, HttpServletRequest request)
	{
		logger.trace("Save invoked with params [Extension: {}, Field: {}]", extensionName, extensionField);
		
		//fetch validate extension point name
		ObjectWrapper<Extension> extensionWrapper = new ObjectWrapper<>();
		ExtensionEntity extensionEntity = extensionUtil.getExtensionEntity(extensionName, extensionWrapper);
		ExtensionPointDetails extensionPointDetails = extensionService.getExtensionPoint(extensionName);
		
		//validate specified model
		validateExtensionField(extensionField);
		
		//if required extension does not exist, created one
		if(extensionEntity == null)
		{
			logger.debug("No extension-entity found for extension '{}' with details - {}. Trying to create new one", extensionName, extensionWrapper.getValue());
			Extension extension = extensionWrapper.getValue();
			
			extensionEntity = new ExtensionEntity(extensionPointDetails.getEntityType().getName(), extension.getOwnerTypeName(), extension.getOwnerId());
			extensionEntity.setName(extension.getName());
			extensionEntity.setAttributes(extension.getAttributes());
			
			extensionService.saveExtensionEntity(extensionEntity);
		}
		
		logger.debug("Saving extension field");
		ExtensionFieldEntity extFieldEntity = WebUtils.convertBean(extensionField, ExtensionFieldEntity.class);
		extensionService.saveExtensionField(extensionEntity.getId(), extFieldEntity);
		
		return new BasicSaveResponse(extFieldEntity.getId());
	}

	/**
	 * Updates specified extension field under specified extension name
	 * @param extensionName Extension name
	 * @param extensionField Field to be updated
	 * @param request Http request
	 * @return Success/failure message
	 */
	@ActionName(ACTION_TYPE_UPDATE)
	@ResponseBody
	@RequestMapping(value = "/update/{" + PARAM_NAME + "}", method = RequestMethod.POST)
	public BaseResponse updateExtensionField(@PathVariable(PARAM_NAME) String extensionName, @RequestBody @Valid ExtensionFieldModel extensionField, HttpServletRequest request)
	{
		logger.trace("Update invoked with params [Extension: {}, Field: {}]", extensionName, extensionField);
		
		if(extensionField.getId() <= 0)
		{
			throw new InvalidRequestParameterException("Invalid/no extension field id specified.");
		}
		
		//fetch validate extension point name
		ObjectWrapper<Extension> extensionWrapper = new ObjectWrapper<>();
		ExtensionEntity extensionEntity = extensionUtil.getExtensionEntity(extensionName, extensionWrapper);
		
		//if required extension does not exist, throw error
		if(extensionEntity == null)
		{
			logger.error("No extension-entity found for extension '{}' with details - {}", extensionName, extensionWrapper.getValue());
			throw new InvalidStateException("No existing extension found with name - {}", extensionName);
		}

		//validate specified model
		validateExtensionField(extensionField);

		//fetch extension id
		long extensionId = extensionService.getExtensionIdForField(extensionField.getId());
		
		if(extensionId != extensionEntity.getId())
		{
			logger.error("Extension name '{}' and specified field id '{}' are not matching", extensionName, extensionField.getId());
			throw new InvalidRequestParameterException("Invalid extension field id specified - " + extensionField.getId());
		}

		logger.debug("Updating extension field");
		ExtensionFieldEntity extFieldEntity = WebUtils.convertBean(extensionField, ExtensionFieldEntity.class);
		extensionService.updateExtensionField(extensionEntity.getId(), extFieldEntity);
		
		return new BaseResponse("Success");
	}

	/**
	 * Deletes extension field with specified id
	 * @param id Extension field id to be deleted
	 * @return Success/failure response
	 */
	@ActionName(ACTION_TYPE_DELETE)
	@ResponseBody
	@RequestMapping(value = "/delete/{" + PARAM_NAME + "}/{" + PARAM_ID + "}", method = RequestMethod.GET)
	public BaseResponse deleteExtensionField(@PathVariable(PARAM_NAME) String extensionName, @PathVariable(PARAM_ID) long id, HttpServletRequest request)
	{
		logger.trace("Delete invoked with params [Extension: {}, Id: {}]", extensionName, id);

		//fetch validate extension point name
		ObjectWrapper<Extension> extensionWrapper = new ObjectWrapper<>();
		ExtensionEntity extensionEntity = extensionUtil.getExtensionEntity(extensionName, extensionWrapper);
		
		//if required extension does not exist, throw error
		if(extensionEntity == null)
		{
			logger.error("No extension-entity found for extension '{}' with details - {}", extensionName, extensionWrapper.getValue());
			throw new InvalidStateException("No existing extension found with name - {}", extensionName);
		}

		//fetch extension id
		long extensionId = extensionService.getExtensionIdForField(id);
		
		//if invalid id is found, throw error
		if(extensionId <= 0)
		{
			logger.error("Invalid extension id encountered for extension field id - " + id);
			throw new InvalidRequestParameterException("Invalid extension field id specified - " + id);
		}
		
		//if extension name and field id are not matching
		if(extensionId != extensionEntity.getId())
		{
			logger.error("Extension name '{}' and specified field id '{}' are not matching", extensionName, id);
			throw new InvalidRequestParameterException("Invalid extension field id specified - " + id);
		}
		
		//invoke delete operation
		extensionService.deleteExtensionField(extensionId, id);
		
		return new BaseResponse("Success");
	}

	/**
	 * Deletes all extension fields of all extensions. Expected to be used for cleanup by test cases/ 
	 * @param request
	 * @return
	 */
	@ActionName(ACTION_TYPE_DELETE_ALL)
	@ResponseBody
	@RequestMapping(value = "/deleteAll", method = RequestMethod.GET)
	public BaseResponse deleteAllExtensionFields(HttpServletRequest request)
	{
		logger.trace("Delete all invoked");
		
		extensionService.deleteAllExtensionFields();
		return new BaseResponse("Success");
	}
}
