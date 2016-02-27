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
import static com.yukthi.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_FETCH_FIELD;
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
import com.yukthi.utils.exceptions.InvalidArgumentException;
import com.yukthi.utils.exceptions.InvalidStateException;
import com.yukthi.webutils.InvalidRequestParameterException;
import com.yukthi.webutils.annotations.ActionName;
import com.yukthi.webutils.common.controllers.IExtensionController;
import com.yukthi.webutils.common.extensions.ExtensionFieldType;
import com.yukthi.webutils.common.models.BaseResponse;
import com.yukthi.webutils.common.models.BasicSaveResponse;
import com.yukthi.webutils.common.models.ExtensionFieldModel;
import com.yukthi.webutils.common.models.ExtensionFieldReadResponse;
import com.yukthi.webutils.common.models.ExtensionFieldsResponse;
import com.yukthi.webutils.extensions.Extension;
import com.yukthi.webutils.extensions.ExtensionOwnerDetails;
import com.yukthi.webutils.extensions.ExtensionPointDetails;
import com.yukthi.webutils.repository.ExtensionEntity;
import com.yukthi.webutils.repository.ExtensionFieldEntity;
import com.yukthi.webutils.security.ISecurityService;
import com.yukthi.webutils.security.UnauthorizedException;
import com.yukthi.webutils.services.ExtensionService;
import com.yukthi.webutils.utils.WebUtils;

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
	private ExtensionUtil extensionUtil;
	
	@Autowired
	private ISecurityService securityService;
	
	@Autowired
	private HttpServletRequest request;
	
	private JsonConverter jsonConverter = new JsonConverter();
	
	/* (non-Javadoc)
	 * @see com.yukthi.webutils.controllers.IExtensionController#fetchExtensionFields(java.lang.String)
	 */
	@Override
	@ActionName(ACTION_TYPE_FETCH)
	@ResponseBody
	@RequestMapping(value = "/fetch/{" + PARAM_NAME + "}", method = RequestMethod.POST)
	public ExtensionFieldsResponse fetchExtensionFields(@PathVariable(PARAM_NAME) String extensionName)
	{
		logger.trace("Fetching extension fields for - {}", extensionName);
		
		if(!securityService.isExtensionAuthorized(extensionService.getExtensionPoint(extensionName)))
		{
			throw new UnauthorizedException("Current user is not authorized to access extesion - {}", extensionName);
		}
		
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
	private void validateExtensionFieldForSave(ExtensionFieldModel extensionField)
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

		//for string fields ensure proper length is specified
		if(extensionField.getType() == ExtensionFieldType.STRING || extensionField.getType() == ExtensionFieldType.MULTI_LINE_STRING)
		{
			if(extensionField.getMaxLength() <= 0 || extensionField.getMaxLength() > 2000)
			{
				logger.error("Invalid length specified for string field. Length should be in the range of [1, 2000]. Specified length - " + extensionField.getMaxLength());
				throw new InvalidRequestParameterException("Invalid length specified for string field. Length should be in the range of [1, 2000]. Specified length - " + extensionField.getMaxLength());
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.yukthi.webutils.controllers.IExtensionController#saveExtensionField(com.yukthi.webutils.common.models.ExtensionFieldModel)
	 */
	@Override
	@ActionName(ACTION_TYPE_SAVE)
	@ResponseBody
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public BasicSaveResponse saveExtensionField(@RequestBody @Valid ExtensionFieldModel extensionField)
	{
		logger.trace("Save invoked with params [Field: {}]", extensionField);

		String extensionName = extensionField.getExtensionName();
		ExtensionPointDetails extensionPointDetails = extensionService.getExtensionPoint(extensionName);
		
		if(extensionPointDetails == null)
		{
			throw new InvalidArgumentException("Invalid extension name specified - {}", extensionName);
		}
		
		if(!securityService.isExtensionAuthorized(extensionPointDetails))
		{
			throw new UnauthorizedException("Current user is not authorized to access extesion - {}", extensionName);
		}
		
		//fetch validate extension point name
		ObjectWrapper<Extension> extensionWrapper = new ObjectWrapper<>();
		ExtensionEntity extensionEntity = extensionUtil.getExtensionEntity(extensionName, extensionWrapper);
		
		//validate specified model
		validateExtensionFieldForSave(extensionField);
		
		//if required extension does not exist, created one
		if(extensionEntity == null)
		{
			logger.debug("No extension-entity found for extension '{}' with details - {}. Trying to create new one", extensionName, extensionWrapper.getValue());
			Extension extension = extensionWrapper.getValue();
			String extensionOwner = null;
			
			if(extension.getOwnerType() != null)
			{
				ExtensionOwnerDetails extensionOwnerDetails = extensionService.getExtensionOwner(extension.getOwnerType());
				
				if(extensionOwnerDetails == null)
				{
					throw new InvalidStateException("Invalid extension owner type provided extension context provider - {}", extension.getOwnerType().getName());
				}
				
				extensionOwner = extensionOwnerDetails.getName();
			}
			
			extensionEntity = new ExtensionEntity(extensionName, extensionOwner, extension.getOwnerId());
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
	 * Ensures the specified field id belongs to the extension specified. And current user is authorized to access
	 * the specified extension.
	 * @param extensionName Extension under which field is being changed
	 * @param fieldId Field being changes
	 * @return Extension id of the specified extension name
	 */
	private long validateFieldForChange(String extensionName, long fieldId)
	{
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
		long extensionId = extensionService.getExtensionIdForField(fieldId);
		
		//if extension name and field id are not matching
		if(extensionId != extensionEntity.getId())
		{
			logger.error("No extension field exists with id {} under extension '{}'", fieldId, extensionName);
			throw new InvalidRequestParameterException("No extension field exists with id {} under extension '{}'", fieldId, extensionName);
		}
		
		//check for authorization
		if(!securityService.isExtensionAuthorized(extensionService.getExtensionPoint(extensionName)))
		{
			throw new UnauthorizedException("Current user is not authorized to access extesion - {}", extensionName);
		}

		return extensionId;
	}

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.controllers.IExtensionController#updateExtensionField(com.yukthi.webutils.common.models.ExtensionFieldModel)
	 */
	@Override
	@ActionName(ACTION_TYPE_UPDATE)
	@ResponseBody
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public BaseResponse updateExtensionField(@RequestBody @Valid ExtensionFieldModel extensionField)
	{
		logger.trace("Update invoked with params [Field: {}]", extensionField);
		
		if(extensionField.getId() <= 0)
		{
			throw new InvalidRequestParameterException("Invalid/no extension field id specified.");
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

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.controllers.IExtensionController#deleteExtensionField(java.lang.String, long)
	 */
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

	/* (non-Javadoc)
	 * @see com.yukthi.webutils.controllers.IExtensionController#readExtensionField(java.lang.String, long)
	 */
	@Override
	@ActionName(ACTION_TYPE_FETCH_FIELD)
	@ResponseBody
	@RequestMapping(value = "/read/{" + PARAM_NAME + "}/{" + PARAM_ID + "}", method = RequestMethod.GET)
	public ExtensionFieldReadResponse readExtensionField(@PathVariable(PARAM_NAME) String extensionName, @PathVariable(PARAM_ID) long fieldId)
	{
		logger.trace("Read field invoked with params [Extension: {}, Id: {}]", extensionName, fieldId);

		if(!securityService.isExtensionAuthorized(extensionService.getExtensionPoint(extensionName)))
		{
			throw new UnauthorizedException("Current user is not authorized to access extesion - {}", extensionName);
		}
		
		ExtensionFieldEntity fieldEntity = extensionService.fetchExtensionField(extensionName, fieldId);
		
		ExtensionFieldModel model = WebUtils.convertBean(fieldEntity, ExtensionFieldModel.class);
		model.setExtensionName(extensionName);
		
		return new ExtensionFieldReadResponse(model);
	}
	
	/* (non-Javadoc)
	 * @see com.yukthi.webutils.controllers.IExtensionController#deleteAllExtensionFields()
	 */
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
}
