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

package com.yukthi.webutils.controllers;

import static com.yukthi.webutils.commons.IActionConstants.ACTION_PREFIX_LOV;
import static com.yukthi.webutils.commons.IActionConstants.ACTION_TYPE_FETCH;
import static com.yukthi.webutils.commons.IActionConstants.PARAM_NAME;
import static com.yukthi.webutils.commons.IActionConstants.PARAM_TYPE;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yukthi.webutils.annotations.ActionName;
import com.yukthi.webutils.commons.LovType;
import com.yukthi.webutils.models.LovListResponse;
import com.yukthi.webutils.services.LovService;

/**
 * Controller for fetching LOV values.
 * @author akiran
 */
@RestController
@ActionName(ACTION_PREFIX_LOV)
@RequestMapping("/lov")
public class LovController
{
	@Autowired
	private LovService lovService;
	
	/**
	 * Service method to fetch LOV values
	 * @param lovName LOV name whose values needs to be fetched
	 * @param type Static or dynamic lov
	 * @param request Current servlet request
	 * @return
	 */
	@ActionName(ACTION_TYPE_FETCH)
	@RequestMapping(value = "/fetch/{" + PARAM_NAME + "}/{" + PARAM_TYPE + "}", method = RequestMethod.GET)
	public LovListResponse fetchLov(@PathVariable(PARAM_NAME) String lovName, @PathVariable(PARAM_TYPE) LovType type, HttpServletRequest request)
	{
		if(type == LovType.STATIC_TYPE)
		{
			return new LovListResponse( lovService.getEnumLovValues(lovName, request.getLocale()) );
		}
		
		return new LovListResponse( lovService.getDynamicLovValues(lovName, request.getLocale()) );
	}
}
