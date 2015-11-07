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

import static com.yukthi.webutils.common.IWebUtilsActionConstants.ACTION_LOV_FETCH;
import static com.yukthi.webutils.common.IWebUtilsActionConstants.PARAM_NAME;
import static com.yukthi.webutils.common.IWebUtilsActionConstants.PARAM_TYPE;

import java.util.List;

import com.yukthi.utils.CommonUtils;
import com.yukthi.utils.rest.RestClient;
import com.yukthi.utils.rest.RestRequest;
import com.yukthi.utils.rest.RestResult;
import com.yukthi.webutils.client.ActionRequestBuilder;
import com.yukthi.webutils.client.ClientContext;
import com.yukthi.webutils.client.RestException;
import com.yukthi.webutils.common.LovType;
import com.yukthi.webutils.common.models.LovListResponse;
import com.yukthi.webutils.common.models.ValueLabel;

/**
 * Helper to provide LOV related functionality
 * @author akiran
 */
public class LovHelper
{
	/**
	 * Helper method to fetch static LOV values
	 * @param context Client context to invoke actions
	 * @param name Name of the static lov to fetch
	 * @return list of LOV values
	 */
	public List<ValueLabel> getStaticLov(ClientContext context, String name)
	{
		RestRequest<?> request = ActionRequestBuilder.buildRequest(context, ACTION_LOV_FETCH, null, CommonUtils.toMap(
				PARAM_NAME, name,
				PARAM_TYPE, LovType.STATIC_TYPE.toString()
		));
		
		RestClient client = context.getRestClient();
		
		RestResult<LovListResponse> lovValuesResult = client.invokeJsonRequest(request, LovListResponse.class);
		LovListResponse response = lovValuesResult.getValue();
		
		if(response == null || response.getCode() != 0)
		{
			throw new RestException("An error occurred while fetching static LOV values", lovValuesResult.getStatusCode(), response);
		}
		
		return response.getLovList();
	}

	/**
	 * Helper method to fetch static LOV values
	 * @param context Client context to invoke actions
	 * @param name Name of the static lov to fetch
	 * @return list of LOV values
	 */
	public List<ValueLabel> getDynamicLov(ClientContext context, String name)
	{
		RestRequest<?> request = ActionRequestBuilder.buildRequest(context, ACTION_LOV_FETCH, null, CommonUtils.toMap(
				PARAM_NAME, name,
				PARAM_TYPE, LovType.DYNAMIC_TYPE.toString()
		));
		
		RestClient client = context.getRestClient();
		
		RestResult<LovListResponse> lovValuesResult = client.invokeJsonRequest(request, LovListResponse.class);
		LovListResponse response = lovValuesResult.getValue();
		
		if(response == null || response.getCode() != 0)
		{
			throw new RestException("An error occurred while fetching dynamic LOV values", lovValuesResult.getStatusCode(), response);
		}
		
		return response.getLovList();
	}
}
