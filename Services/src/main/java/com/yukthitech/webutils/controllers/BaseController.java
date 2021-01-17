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

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

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

import com.yukthitech.persistence.PersistenceException;
import com.yukthitech.webutils.BeanValidationException;
import com.yukthitech.webutils.InvalidRequestParameterException;
import com.yukthitech.webutils.common.IWebUtilsCommonConstants;
import com.yukthitech.webutils.common.models.BaseResponse;
import com.yukthitech.webutils.security.UnauthorizedException;
import com.yukthitech.webutils.services.ExtensionService;
import com.yukthitech.webutils.validation.ExtendableModelValidator;

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
	protected ExtensionService extensionService;
	
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
	//TODO: Needs to handle BeanPropertyBindingResult also properly
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
				responseMsg.append( String.format("Field '%s' [Error - %s]", fldError.getField(), fldError.getDefaultMessage()) ).append("\n");
			}
			else
			{
				responseMsg.append( error.getDefaultMessage() ).append("\n");
			}
		}
		
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		
		return new BaseResponse(IWebUtilsCommonConstants.RESPONSE_CODE_INVALID_REQUEST, responseMsg.toString());
	}
	
	@ExceptionHandler(value={BeanValidationException.class})
	@ResponseBody
	public BaseResponse handleBeanValidationException(HttpServletResponse response, BeanValidationException ex)
	{
		logger.debug("Encountered param-validation exception - " + ex);
		
		//Compute the error message
		List<BeanValidationException.PropertyError> errors = ex.getErrors();
		
		StringBuilder responseMsg = new StringBuilder();
		
		for(BeanValidationException.PropertyError error: errors)
		{
			responseMsg.append( String.format("Field '%s'  [Error - %s]", error.getName(), error.getError()) ).append("\n");
		}
		
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		
		return new BaseResponse(IWebUtilsCommonConstants.RESPONSE_CODE_INVALID_REQUEST, responseMsg.toString());
	}

	/**
	 * Handler for MethodArgumentNotValidException. This exception is expected to be thrown
	 * by spring when request object fails server side validations.
	 * @param response Response object
	 * @param ex Exception to be handled
	 * @return Response with proper error code and message
	 */
	@ExceptionHandler(value={InvalidRequestParameterException.class})
	@ResponseBody
	public BaseResponse handleInvalidRequestParameterException(HttpServletResponse response, InvalidRequestParameterException ex)
	{
		logger.debug("Encountered invalid-request exception - ", ex);

		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		
		return new BaseResponse(IWebUtilsCommonConstants.RESPONSE_CODE_INVALID_REQUEST, fetchMessage(ex));
	}
	
	/**
	 * Handler for UnauthorizedException. This exception is expected to be thrown
	 * when current user is not authorized to execute target operation.
	 * @param response Response object
	 * @param ex Exception to be handled
	 * @return Response with proper error code and message
	 */
	@ExceptionHandler(value={UnauthorizedException.class})
	@ResponseBody
	public BaseResponse handleUnauthorizedException(HttpServletResponse response, UnauthorizedException ex)
	{
		logger.debug("Encountered UnauthorizedException exception - ", ex);

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		
		return new BaseResponse(IWebUtilsCommonConstants.RESPONSE_CODE_AUTHORIZATION_ERROR, fetchMessage(ex));
	}

	/**
	 * Handler to handle persistence exception
	 * @param response
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(value={PersistenceException.class})
	@ResponseBody
	public BaseResponse handlePersistenceException(HttpServletResponse response, PersistenceException ex)
	{
		logger.debug("Encountered PersistenceException exception - ", ex);

		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		
		return new BaseResponse(IWebUtilsCommonConstants.RESPONSE_CODE_INVALID_REQUEST, fetchMessage(ex));
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
		
		return new BaseResponse(IWebUtilsCommonConstants.RESPONSE_CODE_UNHANDLED_SERVER_ERROR, "Unknown server error - " + fetchMessage(ex));
	}
	
	private String fetchMessage(Throwable ex)
	{
		StringBuilder builder = new StringBuilder();
		Map<Throwable, Throwable> processed = new IdentityHashMap<>();
		boolean first = true;
		
		while(ex != null)
		{
			if(!first)
			{
				builder.append("\nCaused by: ");
			}
			
			builder.append(ex.getMessage());
			ex = ex.getCause();
			
			if(processed.containsKey(ex))
			{
				break;
			}
			
			processed.put(ex, ex);
			first = false;
		}
		
		return builder.toString().trim();
	}
}
