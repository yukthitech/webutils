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

package com.yukthitech.webutils.mail.template;

import static com.yukthitech.webutils.common.IWebUtilsActionConstants.ACTION_PREFIX_MAIL_TEMP_CONFIG;
import static com.yukthitech.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_FETCH;
import static com.yukthitech.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_FETCH_NAMES;
import static com.yukthitech.webutils.common.IWebUtilsActionConstants.PARAM_NAME;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.yukthitech.webutils.annotations.ActionName;
import com.yukthitech.webutils.common.mailtemplate.IMailTemplateConfigController;
import com.yukthitech.webutils.common.mailtemplate.MailTemplateConfiguration;
import com.yukthitech.webutils.common.models.BasicReadListResponse;
import com.yukthitech.webutils.common.models.BasicReadResponse;
import com.yukthitech.webutils.controllers.BaseController;

/**
 * Controller for fetching mail template configurations.
 * @author akiran
 */
@RestController
@ActionName(ACTION_PREFIX_MAIL_TEMP_CONFIG)
@RequestMapping("/mail-template-config")
public class MailTemplateConfigController extends BaseController implements IMailTemplateConfigController
{
	/**
	 * Service to fetch template configurations.
	 */
	@Autowired
	private MailTemplateConfigService mailTemplateConfigService;
	
	@Override
	@ActionName(ACTION_TYPE_FETCH_NAMES)
	@ResponseBody
	@RequestMapping(value = "/fetch-names", method = RequestMethod.GET)
	public BasicReadListResponse<String> fetchNames()
	{
		return new BasicReadListResponse<>(mailTemplateConfigService.getMailTemplateConfigurationNames());
	}

	@Override
	@ActionName(ACTION_TYPE_FETCH)
	@ResponseBody
	@RequestMapping(value = "/fetch/{" + PARAM_NAME + "}", method = RequestMethod.GET)
	public BasicReadResponse<MailTemplateConfiguration> fetchConfiguration(@PathVariable(PARAM_NAME) String name)
	{
		return new BasicReadResponse<MailTemplateConfiguration>(mailTemplateConfigService.getMailTemplateConfiguration(name));
	}
}
