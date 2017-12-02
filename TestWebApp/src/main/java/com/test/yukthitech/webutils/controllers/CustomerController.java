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
package com.test.yukthitech.webutils.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.test.yukthitech.webutils.entity.CustomerEntity;
import com.test.yukthitech.webutils.models.TestCustomerModel;
import com.test.yukthitech.webutils.services.CustomerService;
import com.yukthitech.webutils.annotations.ActionName;
import com.yukthitech.webutils.common.models.BaseResponse;
import com.yukthitech.webutils.common.models.BasicSaveResponse;
import com.yukthitech.webutils.controllers.BaseController;
import com.yukthitech.webutils.utils.WebUtils;

/**
 * Test controller to test spring validation enablement
 * @author akiran
 */
@RestController
@RequestMapping("/customer")
@ActionName("customer")
public class CustomerController extends BaseController
{
	@Autowired
	private CustomerService customerService;
	
	@ResponseBody
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ActionName("save")
	public BasicSaveResponse save(@RequestBody TestCustomerModel model)
	{
		CustomerEntity entity = WebUtils.convertBean(model, CustomerEntity.class); 
		customerService.save(entity);
		return new BasicSaveResponse(entity.getId());
	}
	
	@ResponseBody
	@RequestMapping("/fetch/{name}")
	@ActionName("fetch")
	public TestCustomerModel fetch(@PathVariable("name") String name)
	{
		return WebUtils.convertBean(customerService.findByName(name), TestCustomerModel.class);
	}

	@ResponseBody
	@RequestMapping("/deleteAll")
	@ActionName("deleteAll")
	public BaseResponse deleteAll()
	{
		customerService.deleteAll();
		return new BaseResponse();
	}
}
