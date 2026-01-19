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

package com.webutils.services.form.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.webutils.common.form.model.ModelDef;
import com.webutils.common.response.BasicReadResponse;
import com.webutils.services.common.Authorization;
import com.webutils.services.common.InvalidRequestException;
import com.webutils.services.common.NoAuthentication;

/**
 * Controller for fetching LOV values.
 * @author akiran
 */
@RestController
@RequestMapping("/models")
public class ModelController
{
	@Autowired
	private ModelService modelService;
	
	/**
	 * Note: Authentication is handled at model class level using {@link NoAuthentication} and {@link Authorization} annotations.
	 * @param modelName
	 * @return
	 */
	@NoAuthentication
	@RequestMapping(value = "/fetch/{name}", method = RequestMethod.GET)
	public BasicReadResponse<ModelDef> fetchModel(@PathVariable("name") String modelName)
	{
		ModelDef modelDef = modelService.getModelDef(modelName);
		
		if(modelDef == null)
		{
			throw new InvalidRequestException("Invalid model name specified - " + modelName);
		}
		
		return new BasicReadResponse<>(modelDef);
	}
}
