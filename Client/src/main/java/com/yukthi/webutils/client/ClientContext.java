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

import org.apache.http.Header;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthi.utils.rest.GetRestRequest;
import com.yukthi.utils.rest.IRestClientListener;
import com.yukthi.utils.rest.PostRestRequest;
import com.yukthi.utils.rest.RestClient;
import com.yukthi.utils.rest.RestRequest;
import com.yukthi.utils.rest.RestResult;
import com.yukthi.webutils.common.IWebUtilsCommonConstants;
import com.yukthi.webutils.common.models.ActionModel;
import com.yukthi.webutils.common.models.LoginCredentials;
import com.yukthi.webutils.common.models.LoginResponse;

/**
 * Client context to invoke actions
 * @author akiran
 */
public class ClientContext
{
	private static Logger logger = LogManager.getLogger(ClientContext.class);
	
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
	private Map<String, ActionModel> actionsMap;
	
	/**
	 * Authentication token obtained during authentication and other reponses
	 */
	private String authToken;
	
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
	}
	
	/**
	 * Invokes action url and fetches all available actions
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initActions()
	{
		this.actionsMap = new HashMap<>();
		
		GetRestRequest request = new GetRestRequest(actionUrl);
		RestResult<ArrayList<ActionModel>> actionsResult = (RestResult)restClient.invokeJsonRequest(request, ArrayList.class, ActionModel.class);
		
		if(actionsResult.getValue() == null)
		{
			throw new IllegalStateException("Failed to fetch actions. Got response as - " + actionsResult);
		}
		
		for(ActionModel action : actionsResult.getValue())
		{
			this.actionsMap.put(action.getName(), action);
		}
	}
	
	public void authenticate(String userName, String password)
	{
		//build request
		PostRestRequest request = new PostRestRequest(IWebUtilsCommonConstants.LOGIN_URI);
		request.setSecured(true);
		request.setJsonBody(new LoginCredentials(userName, password));
		
		//invoke login api
		RestResult<LoginResponse> authResult = restClient.invokeJsonRequest(request, LoginResponse.class);
		
		//validate response
		if(authResult.getValue() == null || authResult.getValue().getCode() != 0)
		{
			throw new IllegalStateException("Authentication failed. Got response as - " + authResult);
		}
		
		//if successful, cache auth token
		this.authToken = authResult.getValue().getAuthToken();
		
		/*
		 * Add rest client listener to the client which would add auth token to request before sending request
		 * and fetches new auth token from response, if any
		 */
		restClient.setRestClientListener(new IRestClientListener()
		{
			@Override
			public void postrequest(RestRequest<?> request, RestResult<?> result)
			{
				//get auth token header
				Header authHeader = result.getHttpResponse().getFirstHeader(IWebUtilsCommonConstants.HEADER_AUTHORIZATION_TOKEN);
				String newToken = (authHeader != null) ? authHeader.getValue() : null;
				
				//if new auth token is provided
				if(newToken != null && !newToken.equals(ClientContext.this.authToken))
				{
					logger.debug("New token sent by server");
					ClientContext.this.authToken = newToken;
				}
			}

			@Override
			public void prerequest(RestRequest<?> request)
			{
				//set auth token header on request
				request.addHeader(IWebUtilsCommonConstants.HEADER_AUTHORIZATION_TOKEN, ClientContext.this.authToken);
			}
		});
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
		//if actions are not initialized, initialize actions
		if(this.actionsMap == null)
		{
			initActions();
		}
		
		return this.actionsMap.get(name);
	}
}
