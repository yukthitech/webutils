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

package com.yukthitech.webutils.lov;

import static com.yukthitech.webutils.common.IWebUtilsActionConstants.ACTION_PREFIX_LOV;
import static com.yukthitech.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_FETCH;
import static com.yukthitech.webutils.common.IWebUtilsActionConstants.PARAM_NAME;
import static com.yukthitech.webutils.common.IWebUtilsActionConstants.PARAM_TYPE;
import static com.yukthitech.webutils.common.IWebUtilsActionConstants.PARAM_VALUE;

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

import com.yukthitech.webutils.annotations.ActionName;
import com.yukthitech.webutils.annotations.NoAuthentication;
import com.yukthitech.webutils.common.client.IRequestCustomizer;
import com.yukthitech.webutils.common.lov.ILovController;
import com.yukthitech.webutils.common.lov.LovListResponse;
import com.yukthitech.webutils.common.lov.LovType;
import com.yukthitech.webutils.controllers.BaseController;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Controller for fetching LOV values.
 * @author akiran
 */
@RestController
@ActionName(ACTION_PREFIX_LOV)
@RequestMapping("/lov")
public class LovController extends BaseController implements ILovController
{
	/**
	 * Service to fetch lov values.
	 */
	@Autowired
	private LovService lovService;
	
	@Autowired
	private StoredLovService storedLovService;
	
	/**
	 * Current request.
	 */
	@Autowired
	private HttpServletRequest request;
	
	@Value("${webutils.noAuth.static.lovs:}")
	private String noAuthStaticLovsStr;
	
	@Value("${webutils.noAuth.dynamic.lovs:}")
	private String noAuthDynamicLovsStr;
	
	private Set<String> noAuthStaticTypes;
	
	private Set<String> noAuthDynamicNames;
	
	@PostConstruct
	private void init()
	{
		noAuthStaticTypes = StringUtils.isBlank(noAuthStaticLovsStr) ? 
				Collections.emptySet() : 
				new HashSet<String>(Arrays.asList(noAuthStaticLovsStr.trim().split("\\s*\\,\\s*")));

		noAuthDynamicNames = StringUtils.isBlank(noAuthDynamicLovsStr) ? 
				Collections.emptySet() : 
				new HashSet<String>(Arrays.asList(noAuthDynamicLovsStr.trim().split("\\s*\\,\\s*")));
	}

	@Override
	@ActionName(ACTION_TYPE_FETCH)
	@ResponseBody
	@RequestMapping(value = "/fetch/{" + PARAM_NAME + "}/{" + PARAM_TYPE + "}", method = RequestMethod.GET)
	public LovListResponse fetchLov(@PathVariable(PARAM_NAME) String lovName, @PathVariable(PARAM_TYPE) LovType type)
	{
		if(type == LovType.STATIC_TYPE)
		{
			return new LovListResponse( lovService.getEnumLovValues(lovName, request.getLocale()) );
		}
		else if(type == LovType.DYNAMIC_TYPE)
		{
			return new LovListResponse( lovService.getDynamicLovValues(lovName, null, request.getLocale()) );
		}
		
		return new LovListResponse( storedLovService.getLovValues(lovName, request.getLocale()) );
	}

	@NoAuthentication
	@ActionName("noAuthFetch")
	@ResponseBody
	@RequestMapping(value = "/noAuth/fetch/{" + PARAM_NAME + "}/{" + PARAM_TYPE + "}", method = RequestMethod.GET)
	public LovListResponse fetchNoAuthLov(@PathVariable(PARAM_NAME) String lovName, @PathVariable(PARAM_TYPE) LovType type)
	{
		if(type == LovType.STATIC_TYPE)
		{
			if(!noAuthStaticTypes.contains(lovName))
			{
				throw new com.yukthitech.webutils.security.SecurityException(HttpServletResponse.SC_UNAUTHORIZED, 
						"Specified static-LOV cannot be accessed without authentication: {}", lovName);
			}
			
			return new LovListResponse( lovService.getEnumLovValues(lovName, request.getLocale()) );
		}
		
		if(type == LovType.DYNAMIC_TYPE && !noAuthDynamicNames.contains(lovName))
		{
			throw new com.yukthitech.webutils.security.SecurityException(HttpServletResponse.SC_UNAUTHORIZED, 
					"Specified dynamic-LOV cannot be accessed without authentication: {}", lovName);
		}

		return fetchLov(lovName, type);
	}

	@Override
	@ActionName("fetchDependentLov")
	@ResponseBody
	@RequestMapping(value = "/fetchDependentLov/{" + PARAM_NAME + "}/{" + PARAM_TYPE + "}/{" + PARAM_VALUE + "}", method = RequestMethod.GET)
	public LovListResponse fetchDependentLov(@PathVariable(PARAM_NAME) String lovName, 
			@PathVariable(PARAM_TYPE) LovType type, @PathVariable(PARAM_VALUE) String dependencyValue)
	{
		if(type == LovType.DYNAMIC_TYPE)
		{
			return new LovListResponse( lovService.getDynamicLovValues(lovName, dependencyValue, request.getLocale()) );
		}
		
		return new LovListResponse( storedLovService.getLovValues(lovName, dependencyValue, request.getLocale()) );
	}

	@NoAuthentication
	@ActionName("noAuthFetchDependentLov")
	@ResponseBody
	@RequestMapping(value = "/noAuth/fetchDependentLov/{" + PARAM_NAME + "}/{" + PARAM_TYPE + "}/{" + PARAM_VALUE + "}", method = RequestMethod.GET)
	public LovListResponse fetchNoAuthDependentLov(@PathVariable(PARAM_NAME) String lovName, 
			@PathVariable(PARAM_TYPE) LovType type, @PathVariable(PARAM_VALUE) String dependencyValue)
	{
		if(type == LovType.DYNAMIC_TYPE && !noAuthDynamicNames.contains(lovName))
		{
			throw new com.yukthitech.webutils.security.SecurityException(HttpServletResponse.SC_UNAUTHORIZED, 
					"Specified dynamic-LOV cannot be accessed without authentication: {}", lovName);
		}

		return fetchDependentLov(lovName, type, dependencyValue);
	}
	
	/* (non-Javadoc)
	 * @see com.yukthitech.webutils.common.controllers.IClientController#setRequestCustomizer(com.yukthitech.webutils.common.client.IRequestCustomizer)
	 */
	@Override
	public ILovController setRequestCustomizer(IRequestCustomizer customizer)
	{
		return null;
	}
}
