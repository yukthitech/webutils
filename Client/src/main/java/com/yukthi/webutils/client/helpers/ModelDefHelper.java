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

import static com.yukthi.webutils.common.IActionConstants.*;

import com.yukthi.utils.CommonUtils;
import com.yukthi.utils.rest.RestClient;
import com.yukthi.utils.rest.RestRequest;
import com.yukthi.utils.rest.RestResult;
import com.yukthi.webutils.client.ActionRequestBuilder;
import com.yukthi.webutils.client.ClientContext;
import com.yukthi.webutils.client.RestException;
import com.yukthi.webutils.common.models.ModelDefResponse;
import com.yukthi.webutils.common.models.def.ModelDef;

/**
 * Helper to provide Model def related functionality
 * @author akiran
 */
public class ModelDefHelper
{
	/**
	 * Fetches model definition with specified name
	 * @param context Client context
	 * @param name Name of the model to fetch
	 * @return Matching model definition
	 */
	public ModelDef getModelDef(ClientContext context, String name)
	{
		RestRequest<?> request = ActionRequestBuilder.buildRequest(context, ACTION_MODEL_DEF_FETCH, null, CommonUtils.toMap(
				PARAM_NAME, name
		));
		
		RestClient client = context.getRestClient();
		
		RestResult<ModelDefResponse> modelDefResult = client.invokeJsonRequest(request, ModelDefResponse.class);
		ModelDefResponse response = modelDefResult.getValue();
		
		if(response == null || response.getCode() != 0)
		{
			throw new RestException("An error occurred while fetching Model definition for - " + name, modelDefResult.getStatusCode(), response);
		}
		
		return response.getModelDef();
	}
}
