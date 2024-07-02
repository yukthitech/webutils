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

import static com.yukthitech.webutils.common.IWebUtilsActionConstants.ACTION_PREFIX_MODEL_DEF;
import static com.yukthitech.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_FETCH;
import static com.yukthitech.webutils.common.IWebUtilsActionConstants.PARAM_NAME;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.yukthitech.webutils.InvalidRequestParameterException;
import com.yukthitech.webutils.annotations.ActionName;
import com.yukthitech.webutils.annotations.NoAuthentication;
import com.yukthitech.webutils.common.client.IRequestCustomizer;
import com.yukthitech.webutils.common.controllers.IModelController;
import com.yukthitech.webutils.common.models.ModelDefResponse;
import com.yukthitech.webutils.common.models.def.ModelDef;
import com.yukthitech.webutils.services.ModelDetailsService;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Controller for fetching LOV values.
 * @author akiran
 */
@RestController
@ActionName(ACTION_PREFIX_MODEL_DEF)
@RequestMapping("/models")
public class ModelController implements IModelController
{
	@Autowired
	private ModelDetailsService modelService;
	
	@Value("${webutils.noAuth.models:}")
	private String noAuthModelsStr;
	
	private Set<String> noAuthModels;
	
	@PostConstruct
	private void init()
	{
		noAuthModels = StringUtils.isBlank(noAuthModelsStr) ? 
				Collections.emptySet() : 
				new HashSet<String>(Arrays.asList(noAuthModelsStr.trim().split("\\s*\\,\\s*")));
	}
	
	@Override
	@ActionName(ACTION_TYPE_FETCH)
	@RequestMapping(value = "/fetch/{" + PARAM_NAME + "}", method = RequestMethod.GET)
	public ModelDefResponse fetchModel(@PathVariable(PARAM_NAME) String modelName)
	{
		ModelDef modelDef = modelService.getModelDef(modelName);
		
		if(modelDef == null)
		{
			throw new InvalidRequestParameterException("Invalid model name specified - " + modelName);
		}
		
		return new ModelDefResponse(modelDef);
	}
	
	@NoAuthentication
	@ActionName("noAuthFetch")
	@ResponseBody
	@RequestMapping(value = "/noAuth/fetch/{" + PARAM_NAME + "}", method = RequestMethod.GET)
	public ModelDefResponse noAuthFetchModel(@PathVariable(PARAM_NAME) String modelName)
	{
		if(!noAuthModels.contains(modelName))
		{
			throw new com.yukthitech.webutils.security.SecurityException(HttpServletResponse.SC_UNAUTHORIZED, 
					"Specified model cannot be accessed without authentication: {}", modelName);
		}

		return fetchModel(modelName);
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.webutils.common.controllers.IClientController#setRequestCustomizer(com.yukthitech.webutils.common.client.IRequestCustomizer)
	 */
	@Override
	public IModelController setRequestCustomizer(IRequestCustomizer customizer)
	{
		return null;
	}
}
