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

package com.webutils.services.form.lov;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.webutils.common.form.model.LovType;
import com.webutils.common.response.BasicListResponse;
import com.webutils.lov.LovOption;
import com.webutils.services.common.Authorization;
import com.webutils.services.common.InvalidRequestException;
import com.webutils.services.common.NoAuthentication;
import com.webutils.services.form.lov.stored.StoredLovService;
/**
 * Controller for fetching LOV values.
 * @author akiran
 */
@RestController
@RequestMapping("/api/lov")
public class LovController
{
	/**
	 * Service to fetch lov values.
	 */
	@Autowired
	private LovService lovService;
	
	@Autowired
	private StoredLovService storedLovService;
	
	/**
	 * Note: Authentication is handled at model class level using {@link NoAuthentication} and {@link Authorization} annotations.
	 * For stored lov, it is at entity level.
	 * @param type
	 * @param lovName
	 * @return
	 */
	@NoAuthentication
	@ResponseBody
	@RequestMapping(value = "/fetch/{type}/{name}", method = RequestMethod.GET)
	public BasicListResponse<LovOption> fetchLov(@PathVariable("type") String type, @PathVariable("name") String lovName)
	{
		LovType lovType = LovType.getLovType(type);
		
		if(lovType == null)
		{
			throw new InvalidRequestException("Invalid lov type specified: {}", type);
		}
		
		if(lovType == LovType.STATIC_TYPE)
		{
			return new BasicListResponse<LovOption>( lovService.getEnumLovValues(lovName) );
		}
		else if(lovType == LovType.DYNAMIC_TYPE)
		{
			return new BasicListResponse<LovOption>( lovService.getDynamicLovValues(lovName) );
		}
		
		return new BasicListResponse<LovOption>( storedLovService.getLovOptions(lovName) );
	}
}
