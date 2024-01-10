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

import static com.yukthitech.webutils.common.IWebUtilsActionConstants.ACTION_PREFIX_LOV;
import static com.yukthitech.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_FETCH;
import static com.yukthitech.webutils.common.IWebUtilsActionConstants.PARAM_NAME;
import static com.yukthitech.webutils.common.IWebUtilsActionConstants.PARAM_TYPE;
import static com.yukthitech.webutils.common.IWebUtilsActionConstants.PARAM_VALUE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.yukthitech.webutils.annotations.ActionName;
import com.yukthitech.webutils.common.LovType;
import com.yukthitech.webutils.common.client.IRequestCustomizer;
import com.yukthitech.webutils.common.controllers.ILovController;
import com.yukthitech.webutils.common.models.LovListResponse;
import com.yukthitech.webutils.services.LovService;

import jakarta.servlet.http.HttpServletRequest;

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
	
	/**
	 * Current request.
	 */
	@Autowired
	private HttpServletRequest request;
	
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
		
		return new LovListResponse( lovService.getDynamicLovValues(lovName, null, request.getLocale()) );
	}

	@Override
	@ActionName("fetchDependentLov")
	@ResponseBody
	@RequestMapping(value = "/fetchDependentLov/{" + PARAM_NAME + "}/{" + PARAM_VALUE + "}", method = RequestMethod.GET)
	public LovListResponse fetchDependentLov(@PathVariable(PARAM_NAME) String lovName, @PathVariable(PARAM_VALUE) String dependencyValue)
	{
		return new LovListResponse( lovService.getDynamicLovValues(lovName, dependencyValue, request.getLocale()) );
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
