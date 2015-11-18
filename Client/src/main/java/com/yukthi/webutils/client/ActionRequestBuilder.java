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

package com.yukthi.webutils.client;

import java.util.Map;

import com.yukthi.utils.rest.DeleteRestRequest;
import com.yukthi.utils.rest.GetRestRequest;
import com.yukthi.utils.rest.PostRestRequest;
import com.yukthi.utils.rest.RestRequest;
import com.yukthi.webutils.common.HttpMethod;
import com.yukthi.webutils.common.models.ActionModel;

/**
 * Utility to build action requests
 * @author akiran
 */
public class ActionRequestBuilder
{
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
			((GetRestRequest)request).addBeanParameters(requestEntity);
		}
		else if(actionModel.getMethod() == HttpMethod.DELETE)
		{
			request = new DeleteRestRequest(actionModel.getUrl());
		}
		else
		{
			//build POST request
			PostRestRequest postRequest = new PostRestRequest(actionModel.getUrl());
			postRequest.setJsonBody(requestEntity);
			
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
