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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yukthi.utils.CommonUtils;
import com.yukthi.webutils.common.IExtendableModel;
import com.yukthi.webutils.common.IWebUtilsCommonConstants;
import com.yukthi.webutils.common.models.BaseResponse;
import com.yukthi.webutils.repository.ExtensionEntity;
import com.yukthi.webutils.repository.ExtensionFieldEntity;
import com.yukthi.webutils.repository.ExtensionFieldValueEntity;
import com.yukthi.webutils.services.ExtensionService;
import com.yukthi.webutils.validation.ExtendableModelValidator;

/**
 * Base class for all controllers and provides common exception handling
 * @author akiran
 */
public class BaseController
{
	private static Logger logger = LogManager.getLogger(BaseController.class);
	
	@Autowired
	private ExtendableModelValidator extendableModelValidator;
	
	@Autowired
	private ExtensionService extensionService;
	
	@Autowired
	private ExtensionUtil extensionUtil;
	
	@InitBinder
	private void bindExtendedFieldValidator(WebDataBinder binder)
	{
		binder.addValidators(extendableModelValidator);
	}
	
	/**
	 * Handler for MethodArgumentNotValidException. This exception is expected to be thrown
	 * by spring when request object fails server side validations.
	 * @param response Response object
	 * @param ex Exception to be handled
	 * @return Response with proper error code and message
	 */
	@ExceptionHandler(value={MethodArgumentNotValidException.class})
	@ResponseBody
	public BaseResponse handleParamValidationException(HttpServletResponse response, MethodArgumentNotValidException ex)
	{
		logger.debug("Encountered param-validation exception - " + ex);
		
		//Compute the error message
		List<ObjectError> errors = ex.getBindingResult().getAllErrors();
		
		StringBuilder responseMsg = new StringBuilder();
		FieldError fldError = null;
		
		for(ObjectError error: errors)
		{
			if(error instanceof FieldError)
			{
				fldError = (FieldError)error;
				responseMsg.append( String.format("Field '%s'. Error - %s", fldError.getField(), fldError.getDefaultMessage()) ).append("\n");
			}
			else
			{
				responseMsg.append( error.getDefaultMessage() ).append("\n");
			}
		}
		
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		
		return new BaseResponse(IWebUtilsCommonConstants.RESPONSE_CODE_INVALID_REQUEST, responseMsg.toString());
	}
	
	/**
	 * Handler for unhandled exceptions
	 * @param response Response object
	 * @param ex Exception to be handled
	 * @return Response with proper error code and message
	 */
	@ExceptionHandler(value={Exception.class})
	@ResponseBody
	public BaseResponse handleException(HttpServletResponse response, Exception ex)
	{
		logger.error("An uknown exception occurred while processing request: ",  ex);
		
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		
		return new BaseResponse(IWebUtilsCommonConstants.RESPONSE_CODE_UNHANDLED_SERVER_ERROR, "Unknown server error");
	}

	/**
	 * Saves extended field values of the specified model
	 * @param extendableModel Model for which extended values needs to be saved
	 */
	protected void saveExtendedFields(long entityId, IExtendableModel extendableModel)
	{
		//fetch extended values
		Map<Long, String> extendedValues = extendableModel.getExtendedFields();
		
		//if no values are present
		if(extendedValues == null || extendedValues.isEmpty())
		{
			return;
		}
		
		//fetch extension entity
		ExtensionEntity extensionEntity = extensionUtil.getExtensionEntity(extendableModel);
		
		if(extensionEntity == null)
		{
			return;
		}
		
		List<ExtensionFieldValueEntity> existingFieldValues = extensionService.getExtensionValues(extensionEntity.getId(), entityId);
		
		//convert existing values into map
		Map<Long, ExtensionFieldValueEntity> existingValueMap = CommonUtils.buildMap(existingFieldValues, "id", null);
		
		if(existingValueMap == null)
		{
			existingValueMap = Collections.emptyMap();
		}
		
		ExtensionFieldValueEntity valueEntity = null;
		
		//persist the field values
		for(Long fieldId : extendedValues.keySet())
		{
			valueEntity = existingValueMap.get(fieldId);
			
			if(valueEntity != null)
			{
				extensionService.updateExtensionValue(new ExtensionFieldValueEntity(valueEntity.getId(), new ExtensionFieldEntity(fieldId), entityId, extendedValues.get(fieldId)));
			}
			else
			{
				extensionService.saveExtensionValue(new ExtensionFieldValueEntity(0, new ExtensionFieldEntity(fieldId), entityId, extendedValues.get(fieldId)));
			}
		}
	}
	
	/**
	 * Fetches extended field values for specified model and sets them on the
	 * specified model
	 * @param extendableModel Model for which extended field values needs to be fetched
	 */
	protected void fetchExtendedValues(IExtendableModel extendableModel)
	{
		//fetch extension entity
		ExtensionEntity extensionEntity = extensionUtil.getExtensionEntity(extendableModel);
		
		if(extensionEntity == null)
		{
			return;
		}
		
		long id = extendableModel.getId();
		List<ExtensionFieldValueEntity> existingFieldValues = extensionService.getExtensionValues(extensionEntity.getId(), id);
		
		//return if no values found in db for extended fields
		if(CollectionUtils.isEmpty(existingFieldValues))
		{
			return;
		}
		
		//convert existing values into map
		Map<Long, String> existingValueMap = CommonUtils.buildMap(existingFieldValues, "extensionField.id", "value");
		extendableModel.setExtendedFields(existingValueMap);
	}
}
