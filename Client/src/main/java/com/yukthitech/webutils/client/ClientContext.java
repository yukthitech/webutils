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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.hc.core5.http.Header;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.utils.rest.GetRestRequest;
import com.yukthitech.utils.rest.IRestClientListener;
import com.yukthitech.utils.rest.PostRestRequest;
import com.yukthitech.utils.rest.RestClient;
import com.yukthitech.utils.rest.RestRequest;
import com.yukthitech.utils.rest.RestResult;
import com.yukthitech.webutils.common.IWebUtilsCommonConstants;
import com.yukthitech.webutils.common.client.IRequestCustomizer;
import com.yukthitech.webutils.common.models.ActionModel;
import com.yukthitech.webutils.common.models.FetchActionsResponse;
import com.yukthitech.webutils.common.models.auth.LoginCredentials;
import com.yukthitech.webutils.common.models.auth.LoginResponse;

/**
 * Client context to invoke actions.
 * 
 * @author akiran
 */
public class ClientContext
{
	private static Logger logger = LogManager.getLogger(ClientContext.class);

	/**
	 * Rest client that can be used to invoke actions.
	 */
	private RestClient restClient;

	/**
	 * Base url of the application.
	 */
	private String baseUrl;

	/**
	 * Maps name to action details.
	 */
	private Map<String, ActionModel> actionsMap;

	/**
	 * Authentication token obtained during authentication and other responses.
	 */
	private String authToken;

	/**
	 * Used to maintain request customizer at thread level.
	 */
	private ThreadLocal<IRequestCustomizer> requestCustomizerLocal = new ThreadLocal<>();

	/**
	 * The user id.
	 **/
	private Long userId;

	/**
	 * The user name.
	 **/
	private String userName;
	
	/**
	 * Password used for authentication.
	 */
	private String password;
	
	/**
	 * Attributes used during authentication.
	 */
	private Map<String, String> attributes;

	/**
	 * Instantiates a new client context.
	 *
	 * @param baseUrl
	 *            the base url
	 */
	public ClientContext(String baseUrl)
	{
		this.restClient = new RestClient(baseUrl);
		this.restClient.getObjectMapper().setDateFormat(IWebUtilsCommonConstants.DEFAULT_DATE_FORMAT);

		this.baseUrl = baseUrl;
	}

	/**
	 * Invokes action url and fetches all available actions.
	 */
	private void initActions()
	{
		this.actionsMap = new HashMap<>();

		GetRestRequest request = new GetRestRequest(IWebUtilsCommonConstants.ACTIONS_FETCH_URI);
		RestResult<FetchActionsResponse> actionsResult = restClient.invokeJsonRequest(request, FetchActionsResponse.class);

		if(actionsResult.getValue() == null || actionsResult.getValue().getCode() != 0)
		{
			throw new RestException("Failed to fetch actions.", actionsResult.getStatusCode(), actionsResult.getValue());
		}

		for(ActionModel action : actionsResult.getValue().getActions())
		{
			this.actionsMap.put(action.getName(), action);
		}
	}

	/**
	 * Used to set request customizer for the immediate next rest request. For
	 * consequent requests again this method should be called. This method uses
	 * thread-local to make customizer setting thread-safe. That means the
	 * customizer being set will be available only if the request is also being
	 * made with same thread.
	 * 
	 * @param customizer
	 *            Customizer to set for next rest request
	 * @return Current context object
	 */
	public ClientContext setRequestCustomizer(IRequestCustomizer customizer)
	{
		this.requestCustomizerLocal.set(customizer);
		return this;
	}
	
	public String getAuthToken() 
	{
		return authToken;
	}
	
	/**
	 * Used to reauthenticate the session.
	 */
	public void reauthenticate()
	{
		this.authenticate(userName, password, attributes);
	}

	/**
	 * Used for authentication.
	 *
	 * @param userName the user name
	 * @param password the password
	 */
	public void authenticate(String userName, String password)
	{
		this.authenticate(userName, password, null);
	}

	/**
	 * Used for authentication.
	 *
	 * @param userName the user name
	 * @param password the password
	 * @param attributes the attributes
	 */
	public void authenticate(String userName, String password, Map<String, String> attributes)
	{
		// build request
		PostRestRequest request = new PostRestRequest("/auth/login");
		request.setSecured(true);
		request.setJsonBody(new LoginCredentials(userName, password, attributes));

		// invoke login api
		RestResult<LoginResponse> authResult = restClient.invokeJsonRequest(request, LoginResponse.class);

		// validate response
		if(authResult.getValue() == null || authResult.getValue().getCode() != 0)
		{
			throw new IllegalStateException("Authentication failed. Got response as - " + authResult);
		}

		// if successful, cache auth token
		this.authToken = authResult.getValue().getAuthToken();

		this.userName = userName;
		this.password = password;
		this.userId = authResult.getValue().getUserId();

		/*
		 * Add rest client listener to the client which would add auth token to
		 * request before sending request and fetches new auth token from
		 * response, if any
		 */
		restClient.setRestClientListener(new IRestClientListener()
		{
			@Override
			public void postrequest(RestRequest<?> request, RestResult<?> result)
			{
				// get auth token header
				Header authHeader = result.getHttpResponse().getFirstHeader(IWebUtilsCommonConstants.HEADER_AUTHORIZATION_TOKEN);
				String newToken = (authHeader != null) ? authHeader.getValue() : null;

				// if new auth token is provided
				if(newToken != null && !newToken.equals(ClientContext.this.authToken))
				{
					logger.debug("New token sent by server");
					ClientContext.this.authToken = newToken;
				}
			}

			@Override
			public void prerequest(RestRequest<?> request)
			{
				// call the request customizer if any
				IRequestCustomizer requestCustomizer = requestCustomizerLocal.get();

				if(requestCustomizer != null)
				{
					// remove the customizer, so that it will not be used for
					// subsequent requests
					requestCustomizerLocal.remove();

					// invoke the customizer, that can customize the request
					requestCustomizer.customize(request);
				}

				// set auth token header on request
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
	 * Gets the action details with specified name.
	 * 
	 * @param name
	 *            Name of action whose details needs to be fetched
	 * @return Matching action details
	 */
	public ActionModel getAction(String name)
	{
		// if actions are not initialized, initialize actions
		if(this.actionsMap == null)
		{
			initActions();
		}

		return this.actionsMap.get(name);
	}

	/**
	 * Fetches all the available actions.
	 * 
	 * @return all the available actions.
	 */
	Collection<ActionModel> getAllActions()
	{
		// if actions are not initialized, initialize actions
		if(this.actionsMap == null)
		{
			initActions();
		}

		return actionsMap.values();
	}

	/**
	 * Gets the user id.
	 *
	 * @return the user id
	 */
	public Long getUserId()
	{
		return userId;
	}

	/**
	 * Gets the user name.
	 *
	 * @return the user name
	 */
	public String getUserName()
	{
		return userName;
	}
}
