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

package com.yukthi.webutils.client.helpers;

import static com.yukthi.webutils.common.IWebUtilsActionConstants.ACTION_PREFIX_EXTENSIONS;
import static com.yukthi.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_DELETE;
import static com.yukthi.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_DELETE_ALL;
import static com.yukthi.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_FETCH;
import static com.yukthi.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_SAVE;
import static com.yukthi.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_UPDATE;
import static com.yukthi.webutils.common.IWebUtilsActionConstants.PARAM_ID;
import static com.yukthi.webutils.common.IWebUtilsActionConstants.PARAM_NAME;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthi.utils.CommonUtils;
import com.yukthi.utils.rest.RestClient;
import com.yukthi.utils.rest.RestRequest;
import com.yukthi.utils.rest.RestResult;
import com.yukthi.webutils.client.ActionRequestBuilder;
import com.yukthi.webutils.client.ClientContext;
import com.yukthi.webutils.client.RestException;
import com.yukthi.webutils.common.models.BaseResponse;
import com.yukthi.webutils.common.models.BasicSaveResponse;
import com.yukthi.webutils.common.models.ExtensionFieldModel;
import com.yukthi.webutils.common.models.ExtensionFieldsResponse;

/**
 * Helper to provide LOV related functionality
 * @author akiran
 */
public class ExtensionsHelper
{
	private static Logger logger = LogManager.getLogger(ExtensionsHelper.class);
	
	/**
	 * Fetches extension field from extension specified by name "extensionName"
	 * @param context Client Context
	 * @param extensionName Name of the extension for which field needs to be added
	 * @return List of extension fields
	 */
	public List<ExtensionFieldModel> fetchExtensionFields(ClientContext context, String extensionName)
	{
		logger.trace("Trying to fetch extension fields for extension - {}", extensionName);
		
		RestRequest<?> request = ActionRequestBuilder.buildRequest(context, ACTION_PREFIX_EXTENSIONS + "." + ACTION_TYPE_FETCH , null, CommonUtils.toMap(
				PARAM_NAME, extensionName
		));
		
		RestClient client = context.getRestClient();
		
		RestResult<ExtensionFieldsResponse> extResult = client.invokeJsonRequest(request, ExtensionFieldsResponse.class);
		ExtensionFieldsResponse response = extResult.getValue();
		
		if(response == null || response.getCode() != 0)
		{
			throw new RestException("An error occurred while fetching extension fields", extResult.getStatusCode(), response);
		}
		
		return response.getExtensionFields();
	}

	/**
	 * Adds specified extension field for specified extension.
	 * @param context Client context
	 * @param extensionField Field to be added
	 * @return Id of the newly added field.
	 */
	public long addExtensionField(ClientContext context, ExtensionFieldModel extensionField)
	{
		logger.trace("Trying to add extension field - {}", extensionField);
		
		RestRequest<?> request = ActionRequestBuilder.buildRequest(context, ACTION_PREFIX_EXTENSIONS + "." + ACTION_TYPE_SAVE , extensionField, null);
		
		RestClient client = context.getRestClient();
		
		RestResult<BasicSaveResponse> extResult = client.invokeJsonRequest(request, BasicSaveResponse.class);
		BasicSaveResponse response = extResult.getValue();
		
		if(response == null || response.getCode() != 0)
		{
			logger.error("An error occurred while saving extension field. Response - {} ", response);
			throw new RestException("An error occurred while saving extension field", extResult.getStatusCode(), response);
		}
	
		logger.debug("Successfully added extension field. Id - {}", response.getId());
		return response.getId();
	}
	
	/**
	 * Updates specified extension field under specified extension
	 * @param context Client context
	 * @param extensionField Field to be updated
	 */
	public void updateExtensionField(ClientContext context, ExtensionFieldModel extensionField)
	{
		logger.trace("Trying to update extension field - {}", extensionField);
		
		RestRequest<?> request = ActionRequestBuilder.buildRequest(context, ACTION_PREFIX_EXTENSIONS + "." + ACTION_TYPE_UPDATE , extensionField, null);
		
		RestClient client = context.getRestClient();
		
		RestResult<BaseResponse> extResult = client.invokeJsonRequest(request, BaseResponse.class);
		BaseResponse response = extResult.getValue();
		
		if(response == null || response.getCode() != 0)
		{
			logger.error("An error occurred while updating extension field. Response - {} ", response);
			throw new RestException("An error occurred while updating extension field", extResult.getStatusCode(), response);
		}
	
		logger.debug("Successfully updated extension field.");
	}
	
	/**
	 * Deletes extension field with specified id
	 * @param context Client Context
	 * @param extensionName Extension from which field needs to be deleted
	 * @param extensionFieldId Extension field id to be deleted
	 */
	public void deleteExtensionField(ClientContext context, String extensionName, long extensionFieldId)
	{
		logger.trace("Trying to delete extension field - {}", extensionFieldId);
		
		RestRequest<?> request = ActionRequestBuilder.buildRequest(context, ACTION_PREFIX_EXTENSIONS + "." + ACTION_TYPE_DELETE , null, CommonUtils.toMap(
				PARAM_ID, "" + extensionFieldId,
				PARAM_NAME, extensionName
		));
		
		RestClient client = context.getRestClient();
		
		RestResult<BaseResponse> extResult = client.invokeJsonRequest(request, BaseResponse.class);
		BaseResponse response = extResult.getValue();
		
		if(response == null || response.getCode() != 0)
		{
			logger.error("An error occurred while deleting extension field. Response - {} ", response);
			throw new RestException("An error occurred while deleting extension field", extResult.getStatusCode(), response);
		}
	
		logger.debug("Successfully deleted field - {}.", extensionFieldId);
	}

	/**
	 * Deletes extension fields of all extensions. Expected to be used by test cases for cleanup.
	 * @param context Client context
	 */
	public void deleteAllExtensionFields(ClientContext context)
	{
		logger.trace("Trying to delete all extension fields");
		
		RestRequest<?> request = ActionRequestBuilder.buildRequest(context, ACTION_PREFIX_EXTENSIONS + "." + ACTION_TYPE_DELETE_ALL , null, null);
		
		RestClient client = context.getRestClient();
		
		RestResult<BaseResponse> extResult = client.invokeJsonRequest(request, BaseResponse.class);
		BaseResponse response = extResult.getValue();
		
		if(response == null || response.getCode() != 0)
		{
			logger.error("An error occurred while deleting extension fields. Response - {} ", response);
			throw new RestException("An error occurred while deleting extension fields", extResult.getStatusCode(), response);
		}
	
		logger.debug("Successfully deleted all extension fields.");
	}
}
