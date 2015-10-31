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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.yukthi.utils.rest.GetRestRequest;
import com.yukthi.utils.rest.RestClient;
import com.yukthi.utils.rest.RestResult;
import com.yukthi.webutils.models.ActionModel;

/**
 * Client context to invoke actions
 * @author akiran
 */
public class ClientContext
{
	/**
	 * Rest client that can be used to invoke actions
	 */
	private RestClient restClient;
	
	/**
	 * Base url of the application
	 */
	private String baseUrl;
	
	/**
	 * Action url of the application
	 */
	private String actionUrl;
	
	/**
	 * Maps name to action details
	 */
	private Map<String, ActionModel> actionsMap = new HashMap<>();
	
	/**
	 * Instantiates a new client context.
	 *
	 * @param baseUrl the base url
	 * @param actionUrl the action url
	 */
	public ClientContext(String baseUrl, String actionUrl)
	{
		this.restClient = new RestClient(baseUrl);
		
		this.baseUrl = baseUrl;
		this.actionUrl = actionUrl;
		
		this.init();
	}
	
	/**
	 * Invokes action url and fetches all available actions
	 */
	@SuppressWarnings("unchecked")
	private void init()
	{
		GetRestRequest request = new GetRestRequest(actionUrl);
		RestResult<ArrayList<ActionModel>> actionsResult = restClient.invokeJsonRequest(request, ArrayList.class, ActionModel.class);
		
		if(actionsResult.getValue() == null)
		{
			throw new IllegalStateException("Failed to fetch actions. Got response as - " + actionsResult);
		}
		
		for(ActionModel action : actionsResult.getValue())
		{
			this.actionsMap.put(action.getName(), action);
		}
	}

	/**
	 * Gets the rest client that can be used to invoke actions.
	 *
	 * @return the rest client that can be used to invoke actions
	 */
	public RestClient getRestClient()
	{
		return restClient;
	}

	/**
	 * Gets the base url of the application.
	 *
	 * @return the base url of the application
	 */
	public String getBaseUrl()
	{
		return baseUrl;
	}

	/**
	 * Gets the action url of the application.
	 *
	 * @return the action url of the application
	 */
	public String getActionUrl()
	{
		return actionUrl;
	}
	
	/**
	 * Gets the action details with specified name
	 * @param name Name of action whose details needs to be fetched
	 * @return Matching action details
	 */
	public ActionModel getAction(String name)
	{
		return this.actionsMap.get(name);
	}
}
