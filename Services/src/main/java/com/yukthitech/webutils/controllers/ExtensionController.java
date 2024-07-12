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

package com.yukthitech.webutils.controllers;

import static com.yukthitech.webutils.common.IWebUtilsActionConstants.ACTION_PREFIX_EXTENSIONS;
import static com.yukthitech.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_DELETE;
import static com.yukthitech.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_DELETE_ALL;
import static com.yukthitech.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_FETCH;
import static com.yukthitech.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_FETCH_FIELD;
import static com.yukthitech.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_SAVE;
import static com.yukthitech.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_UPDATE;
import static com.yukthitech.webutils.common.IWebUtilsActionConstants.PARAM_ID;
import static com.yukthitech.webutils.common.IWebUtilsActionConstants.PARAM_NAME;

import java.util.List;

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

import com.yukthitech.persistence.annotations.DataType;
import com.yukthitech.persistence.conversion.impl.JsonWithTypeConverter;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.webutils.IWebUtilsInternalConstants;
import com.yukthitech.webutils.InvalidRequestException;
import com.yukthitech.webutils.annotations.ActionName;
import com.yukthitech.webutils.common.client.IRequestCustomizer;
import com.yukthitech.webutils.common.controllers.IExtensionController;
import com.yukthitech.webutils.common.extensions.ExtensionFieldType;
import com.yukthitech.webutils.common.models.BaseResponse;
import com.yukthitech.webutils.common.models.BasicSaveResponse;
import com.yukthitech.webutils.common.models.ExtensionFieldModel;
import com.yukthitech.webutils.common.models.ExtensionFieldReadResponse;
import com.yukthitech.webutils.common.models.ExtensionFieldsResponse;
import com.yukthitech.webutils.extensions.ExtensionDetails;
import com.yukthitech.webutils.extensions.ExtensionEntityDetails;
import com.yukthitech.webutils.repository.ExtensionEntity;
import com.yukthitech.webutils.repository.ExtensionFieldEntity;
import com.yukthitech.webutils.security.ISecurityService;
import com.yukthitech.webutils.security.UnauthorizedException;
import com.yukthitech.webutils.services.ExtensionService;
import com.yukthitech.webutils.utils.WebUtils;

import jakarta.validation.Valid;

/**
 * Controller for fetching LOV values.
 * @author akiran
 */
@RestController
@ActionName(ACTION_PREFIX_EXTENSIONS)
@RequestMapping("/extensions")
public class ExtensionController extends BaseController implements IExtensionController
{
	private static Logger logger = LogManager.getLogger(ExtensionController.class);
	
	@Autowired
	private ExtensionService extensionService;
	
	@Autowired
	private ISecurityService securityService;
	
	private JsonWithTypeConverter jsonConverter = new JsonWithTypeConverter();
	
	@Autowired(required = false)
	private IExtensionContextProvider extensionContextProvider;

	@Override
	@ActionName(ACTION_TYPE_FETCH)
	@ResponseBody
	@RequestMapping(value = "/fetch/{" + PARAM_NAME + "}", method = RequestMethod.POST)
	public ExtensionFieldsResponse fetchExtensionFields(@PathVariable(PARAM_NAME) String extensionName)
	{
		logger.trace("Fetching extension fields for - {}", extensionName);
		
		if(!extensionService.isValidExtension(extensionName))
		{
			logger.debug("No extension entity found for extension - {}", extensionName);
			throw new InvalidRequestException("No extension found with specified name - " + extensionName);
		}
		
		//fetch extension fields and build response
		List<ExtensionFieldEntity> extensionFields = extensionService.getExtensionFields(extensionName);
		logger.debug("Found {} extension fields", (extensionFields != null) ? extensionFields.size() : 0);
		
		List<ExtensionFieldModel> extensionFieldModels = WebUtils.convertBeans(extensionFields, ExtensionFieldModel.class);
		
		return new ExtensionFieldsResponse(extensionFieldModels);
	}

	/**
	 * Validates specified extension field model. Throws exception if validation fails.
	 * @param extensionField Extension field to validated.
	 */
	private void validateExtensionFieldForSave(ExtensionFieldModel extensionField)
	{
		if(extensionField.getType() == ExtensionFieldType.LIST_OF_VALUES)
		{
			if(CollectionUtils.isEmpty(extensionField.getLovOptions()))
			{
				logger.error("No LOV options specified for lov field");
				throw new InvalidRequestException("No LOV options specified for LOV field");
			}
			
			String lovOptStr = (String) jsonConverter.convertToDBType(extensionField.getLovOptions(), DataType.STRING);
			
			if(lovOptStr.length() > IWebUtilsInternalConstants.MAX_EXT_FIELD_LENGTH)
			{
				logger.error("Too many or too long lov options specified. Got result json string length as - ", lovOptStr.length());
				throw new InvalidRequestException("Too many or too long LOV options specified");
			}
		}

		//for string fields ensure proper length is specified
		if(extensionField.getType() == ExtensionFieldType.STRING || extensionField.getType() == ExtensionFieldType.MULTI_LINE_STRING)
		{
			if(extensionField.getMaxLength() <= 0 || extensionField.getMaxLength() > IWebUtilsInternalConstants.MAX_EXT_FIELD_LENGTH)
			{
				logger.error("Invalid length specified for string field. Length should be in the range of [1, {}]. Specified length - {}", 
						IWebUtilsInternalConstants.MAX_EXT_FIELD_LENGTH, extensionField.getMaxLength());
				throw new InvalidRequestException("Invalid length specified for string field. Length should be in the range of [1, {}]. Specified length - {}",
						IWebUtilsInternalConstants.MAX_EXT_FIELD_LENGTH, extensionField.getMaxLength());
			}
		}
	}
	
	@Override
	@ActionName(ACTION_TYPE_SAVE)
	@ResponseBody
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public BasicSaveResponse saveExtensionField(@RequestBody @Valid ExtensionFieldModel extensionField)
	{
		logger.trace("Save invoked with params [Field: {}]", extensionField);

		String extensionName = extensionField.getExtensionName();
		ExtensionEntityDetails extensionPointDetails = extensionService.getExtensionEntityDetailsByName(extensionName);
		
		if(extensionPointDetails == null)
		{
			throw new InvalidArgumentException("Invalid extension name specified - {}", extensionName);
		}
		
		if(!securityService.isExtensionAuthorized(extensionPointDetails))
		{
			throw new UnauthorizedException("Current user is not authorized to access extesion - {}", extensionName);
		}
		
		//fetch validate extension point name
		ExtensionEntity extensionEntity = extensionService.getExtensionEntity(extensionName);
		
		//validate specified model
		validateExtensionFieldForSave(extensionField);
		
		//if required extension does not exist, created one
		if(extensionEntity == null)
		{
			logger.debug("No extension-entity found for extension '{}' with default owner. Trying to create new one", extensionName);
			ExtensionDetails extensionDetails = extensionContextProvider.getExtensionDetails(extensionName, extensionPointDetails);
			
			extensionEntity = new ExtensionEntity();
			extensionEntity.setName(extensionField.getExtensionName());
			
			if(extensionDetails.getOwnerType() != null)
			{
				extensionEntity.setOwnerEntityType(extensionDetails.getOwnerType().getName());
				extensionEntity.setOwnerEntityId(extensionDetails.getOwnerId());
			}
			
			extensionEntity.setAttributes(extensionDetails.getAttributes());
			extensionEntity.setTargetEntityType(extensionPointDetails.getEntityType().getName());
			
			extensionService.saveExtensionEntity(extensionEntity);
		}
		
		logger.debug("Saving extension field");
		ExtensionFieldEntity extFieldEntity = WebUtils.convertBean(extensionField, ExtensionFieldEntity.class);
		extensionService.saveExtensionField(extensionEntity.getId(), extFieldEntity);
		
		return new BasicSaveResponse(extFieldEntity.getId());
	}
	
	/**
	 * Ensures the specified field id belongs to the extension specified. And current user is authorized to access
	 * the specified extension.
	 * @param extensionName Extension under which field is being changed
	 * @param fieldId Field being changes
	 * @return Extension id of the specified extension name
	 */
	private long validateFieldForChange(String extensionName, long fieldId)
	{
		ExtensionEntityDetails extensionEntityDetails = extensionService.getExtensionEntityDetailsByName(extensionName);
		
		//check for authorization
		if(!securityService.isExtensionAuthorized(extensionEntityDetails))
		{
			throw new UnauthorizedException("Current user is not authorized to access extesion - {}", extensionName);
		}

		//fetch validate extension point name
		ExtensionEntity extensionEntity = extensionService.getExtensionEntity(extensionName);
		
		//if required extension does not exist, throw error
		if(extensionEntity == null)
		{
			logger.error("No extension-entity found for extension '{}'", extensionName);
			throw new InvalidStateException("No existing extension found with name - {}", extensionName);
		}

		//fetch extension id
		long extensionId = extensionEntity.getId();
		
		//if extension name and field id are not matching
		if(extensionId != extensionEntity.getId())
		{
			logger.error("No extension field exists with id {} under extension '{}'", fieldId, extensionName);
			throw new InvalidRequestException("No extension field exists with id {} under extension '{}'", fieldId, extensionName);
		}

		return extensionId;
	}

	@Override
	@ActionName(ACTION_TYPE_UPDATE)
	@ResponseBody
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public BaseResponse updateExtensionField(@RequestBody @Valid ExtensionFieldModel extensionField)
	{
		logger.trace("Update invoked with params [Field: {}]", extensionField);
		
		if(extensionField.getId() <= 0)
		{
			throw new InvalidRequestException("Invalid/no extension field id specified.");
		}

		long extensionId = validateFieldForChange(extensionField.getExtensionName(), extensionField.getId());

		//validate specified model
		validateExtensionFieldForSave(extensionField);

		logger.debug("Updating extension field");
		ExtensionFieldEntity extFieldEntity = WebUtils.convertBean(extensionField, ExtensionFieldEntity.class);
		extFieldEntity.setExtension(new ExtensionEntity(extensionId));
		
		extensionService.updateExtensionField(extFieldEntity);
		
		return new BaseResponse("Success");
	}

	@Override
	@ActionName(ACTION_TYPE_DELETE)
	@ResponseBody
	@RequestMapping(value = "/delete/{" + PARAM_NAME + "}/{" + PARAM_ID + "}", method = RequestMethod.GET)
	public BaseResponse deleteExtensionField(@PathVariable(PARAM_NAME) String extensionName, @PathVariable(PARAM_ID) long fieldId)
	{
		logger.trace("Delete invoked with params [Extension: {}, Id: {}]", extensionName, fieldId);

		validateFieldForChange(extensionName, fieldId);
		
		//invoke delete operation
		extensionService.deleteExtensionField(fieldId);
		
		return new BaseResponse("Success");
	}

	@Override
	@ActionName(ACTION_TYPE_FETCH_FIELD)
	@ResponseBody
	@RequestMapping(value = "/read/{" + PARAM_NAME + "}/{" + PARAM_ID + "}", method = RequestMethod.GET)
	public ExtensionFieldReadResponse readExtensionField(@PathVariable(PARAM_NAME) String extensionName, @PathVariable(PARAM_ID) long fieldId)
	{
		logger.trace("Read field invoked with params [Extension: {}, Id: {}]", extensionName, fieldId);
		
		ExtensionEntityDetails extensionEntityDetails = extensionService.getExtensionEntityDetailsByName(extensionName);

		if(!securityService.isExtensionAuthorized(extensionEntityDetails))
		{
			throw new UnauthorizedException("Current user is not authorized to access extesion - {}", extensionName);
		}
		
		ExtensionFieldEntity fieldEntity = extensionService.fetchExtensionField(extensionName, fieldId);
		
		ExtensionFieldModel model = WebUtils.convertBean(fieldEntity, ExtensionFieldModel.class);
		model.setExtensionName(extensionName);
		
		return new ExtensionFieldReadResponse(model);
	}
	
	@Override
	@ActionName(ACTION_TYPE_DELETE_ALL)
	@ResponseBody
	@RequestMapping(value = "/deleteAll", method = RequestMethod.GET)
	public BaseResponse deleteAllExtensionFields()
	{
		logger.trace("Delete all invoked");
		
		extensionService.deleteAllExtensionFields();
		return new BaseResponse("Success");
	}
	
	/* (non-Javadoc)
	 * @see com.yukthitech.webutils.common.controllers.IClientController#setRequestCustomizer(com.yukthitech.webutils.common.client.IRequestCustomizer)
	 */
	@Override
	public IExtensionController setRequestCustomizer(IRequestCustomizer customizer)
	{
		return null;
	}
}
