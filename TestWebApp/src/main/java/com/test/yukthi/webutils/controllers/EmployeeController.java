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
package com.test.yukthi.webutils.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.test.yukthi.webutils.entity.EmployeeEntity;
import com.test.yukthi.webutils.models.EmployeeModel;
import com.test.yukthi.webutils.services.EmployeeService;
import com.yukthi.webutils.InvalidRequestParameterException;
import com.yukthi.webutils.annotations.ActionName;
import com.yukthi.webutils.common.models.BaseResponse;
import com.yukthi.webutils.common.models.BasicCountResponse;
import com.yukthi.webutils.common.models.BasicReadResponse;
import com.yukthi.webutils.common.models.BasicSaveResponse;
import com.yukthi.webutils.controllers.BaseController;
import com.yukthi.webutils.utils.WebUtils;

/**
 * Test controller to test spring validation enablement
 * @author akiran
 */
@RestController
@RequestMapping("/employee")
@ActionName("employee")
public class EmployeeController extends BaseController
{
	@Autowired
	private EmployeeService service;
	
	@ResponseBody
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ActionName("save")
	public BasicSaveResponse save(@RequestBody @Valid EmployeeModel model)
	{
		EmployeeEntity entity = WebUtils.convertBean(model, EmployeeEntity.class); 
		service.save(entity, model);
		
		return new BasicSaveResponse(entity.getId());
	}
	
	@ResponseBody
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@ActionName("update")
	public BasicSaveResponse update(@RequestBody @Valid EmployeeModel model)
	{
		if(model.getId() == null || model.getId() <= 0)
		{
			throw new InvalidRequestParameterException("Invalid id specified for update: " + model.getId());
		}
		
		EmployeeEntity entity = WebUtils.convertBean(model, EmployeeEntity.class); 
		service.update(entity, model);
		
		return new BasicSaveResponse(entity.getId());
	}
	
	@ResponseBody
	@RequestMapping("/fetch/{id}")
	@ActionName("fetch")
	public BasicReadResponse<EmployeeModel> fetch(@PathVariable("id") long id)
	{
		EmployeeModel model = service.fetchFullModel(id, EmployeeModel.class);
		return new BasicReadResponse<EmployeeModel>(model);
	}
	
	@ResponseBody
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
	@ActionName("delete")
	public BaseResponse delete(@PathVariable("id") long id)
	{
		service.deleteById(id);
		
		return new BaseResponse();
	}

	@ResponseBody
	@RequestMapping("/deleteAll")
	@ActionName("deleteAll")
	public BaseResponse deleteAll()
	{
		service.deleteAll();
		return new BaseResponse();
	}

	@ResponseBody
	@RequestMapping("/count")
	@ActionName("count")
	public BasicCountResponse count()
	{
		return new BasicCountResponse(service.getCount());
	}
}
