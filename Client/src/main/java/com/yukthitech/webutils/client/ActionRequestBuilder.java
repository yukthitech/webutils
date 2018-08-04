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

package com.yukthitech.webutils.client;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.Tika;

import com.yukthitech.utils.beans.BeanInfo;
import com.yukthitech.utils.beans.PropertyMapper;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.utils.rest.DeleteRestRequest;
import com.yukthitech.utils.rest.GetRestRequest;
import com.yukthitech.utils.rest.PostRestRequest;
import com.yukthitech.utils.rest.RestRequest;
import com.yukthitech.webutils.common.FileInfo;
import com.yukthitech.webutils.common.HttpMethod;
import com.yukthitech.webutils.common.IWebUtilsCommonConstants;
import com.yukthitech.webutils.common.models.ActionModel;

/**
 * Utility to build action requests
 * @author akiran
 */
public class ActionRequestBuilder
{
	private static Logger logger = LogManager.getLogger(ActionRequestBuilder.class);
	
	private static final Tika tika = new Tika();
	
	@SuppressWarnings("rawtypes")
	private static Object getFieldValue(Object entity, String field) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		if(entity instanceof Map)
		{
			return ((Map)entity).get(field);
		}
		
		BeanInfo beanInfo = PropertyMapper.getBeanInfo(entity.getClass());
		return beanInfo.getProperty(field).getProperty().getValue(entity);		
	}
	
	/**
	 * Process each file field. Removes files from fields and add them as attachments (multi parts) to request
	 * @param requestEntity
	 * @param field
	 * @param fileMap
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void processFileField(Object requestEntity, String field, IdentityHashMap<String, File> fileMap) throws Exception
	{
		Object fileFieldValue = getFieldValue(requestEntity, field);
		
		//if field value is null, ignore
		if(fileFieldValue == null)
		{
			return;
		}
		
		//if file is single valued
		if(fileFieldValue instanceof FileInfo)
		{
			FileInfo fileInfo = (FileInfo)fileFieldValue;
			
			//if file info is meta info only (no file), ignore
			if(fileInfo.getFile() == null)
			{
				return;
			}
			
			//remove file from field and add it as attachment to request
			fileMap.put(new String(field), fileInfo.getFile());
			PropertyUtils.setProperty(requestEntity, field, null);
		}
		//if field is multi file valued
		else
		{
			List<FileInfo> fileInfoLst = (List)fileFieldValue;
			List<FileInfo> newFileInfoLst = new ArrayList<>();
			
			for(FileInfo fileInfoObj : fileInfoLst)
			{
				//if file info is meta info only (no file), ignore
				if(fileInfoObj.getFile() == null)
				{
					newFileInfoLst.add(fileInfoObj);
					continue;
				}
			
				fileMap.put(new String(field), fileInfoObj.getFile());
			}
			
			if(newFileInfoLst.isEmpty())
			{
				newFileInfoLst = null;
			}
			
			PropertyUtils.setProperty(requestEntity, field, newFileInfoLst);
		}
	}
	
	/**
	 * Builds REST request for specified action and add request parameters and entity as required.
	 * @param context Context used to fetch action details
	 * @param action Action name to be invoked
	 * @param requestEntity Request entity to set as body for POST method
	 * @param parameters Parameters to be used in url and request parameters
	 * @return Rest result from server
	 */
	public static RestRequest<?> buildRequest(ClientContext context, String action, Object requestEntity, Map<String, ? extends Object> parameters)
	{
		logger.trace("Building request object for action - {}", action);
		
		ActionModel actionModel = context.getAction(action);
		
		if(actionModel == null)
		{
			throw new IllegalArgumentException("Invalid action name specified - " + action);
		}
		
		RestRequest<?> request = null;
		
		if(actionModel.getMethod() == HttpMethod.GET)
		{
			//build GET request
			request = new GetRestRequest(actionModel.getUrl());
			
			if(requestEntity != null)
			{
				((GetRestRequest) request).addBeanParameters(requestEntity);
			}
			
			if(parameters != null)
			{
				Object value = null;
				
				for(String key : parameters.keySet())
				{
					value = parameters.get(key);
					
					if(value == null)
					{
						continue;
					}
					
					((GetRestRequest) request).addParam(key, "" + value);
				}
			}
		}
		else if(actionModel.getMethod() == HttpMethod.DELETE)
		{
			request = new DeleteRestRequest(actionModel.getUrl());
		}
		else
		{
			//build POST request
			PostRestRequest postRequest = new PostRestRequest(actionModel.getUrl());
			
			//if attachments are expected
			if(actionModel.isAttachmentsExpected())
			{
				logger.trace("Found files on action request entity. Building multi part request. Action - {}", action);
				
				//build multi part request
				postRequest.setMultipartRequest(true);
				Set<String> fileFields = actionModel.getFileFields();
				IdentityHashMap<String, File> attachments = new IdentityHashMap<>();
				File file = null;
				
				//process each file field 
				for(String field : fileFields)
				{
					try
					{
						processFileField(requestEntity, field, attachments);
					}catch(Exception ex)
					{
						throw new InvalidStateException(ex, "An error occurred while processing file field - {}", field);
					}
				}
				
				postRequest.addJsonPart(IWebUtilsCommonConstants.MULTIPART_DEFAULT_PART, requestEntity);
				
				for(String field : attachments.keySet())
				{
					file = attachments.get(field);
					
					try
					{
						postRequest.addAttachment(field, file, tika.detect(file));
					}catch(Exception ex)
					{
						logger.info("An error occurred while fetching content type of file - {}", file.getPath());
						postRequest.addAttachment(field, file, "application/octet-stream");
					}
				}
			}
			else
			{
				if(requestEntity != null)
				{
					postRequest.setJsonBody(requestEntity);
				}
			}
			
			request = postRequest;
		}
		
		//add all required url parameters
		if(actionModel.getUrlParameters() != null)
		{
			for(String param: actionModel.getUrlParameters())
			{
				//if required url param is not provided
				if(!parameters.containsKey(param))
				{
					throw new IllegalArgumentException("Required url-param is not specified with name - " + param);
				}
				
				request.addPathVariable(param, "" + parameters.get(param));
			}
		}
		
		//add all request parameters
		if(actionModel.getRequestParameters() != null)
		{
			for(String param : actionModel.getRequestParameters())
			{
				request.addParam(param, "" + parameters.get(param));
			}
		}
		
		return request;
	}
 }
