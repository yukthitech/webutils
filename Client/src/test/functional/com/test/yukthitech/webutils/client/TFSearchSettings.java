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

package com.test.yukthitech.webutils.client;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.yukthitech.webutils.common.IWebUtilsCommonConstants;
import com.yukthitech.webutils.common.models.BasicReadResponse;
import com.yukthitech.webutils.common.models.BasicSaveResponse;
import com.yukthitech.webutils.common.search.ISearchSettingsController;
import com.yukthitech.webutils.common.search.SearchSettingsColumn;
import com.yukthitech.webutils.common.search.SearchSettingsModel;

/**
 * @author akiran
 *
 */
public class TFSearchSettings extends TFBase
{
	private ISearchSettingsController searchSettingsController;
	
	@BeforeClass
	public void setup()
	{
		searchSettingsController = super.clientControllerFactory.getController(ISearchSettingsController.class);
	}
	
	private SearchSettingsModel getSearchSettings(String queryName)
	{
		BasicReadResponse<SearchSettingsModel> response = searchSettingsController.fetch(queryName);
		return response.getModel();
	}
	
	@Test
	public void testSave()
	{
		SearchSettingsModel model = new SearchSettingsModel();
		model.setSearchQueryName("empSearch");
		model.setPageSize(20);
		model.addSearchColumn(new SearchSettingsColumn("Name", true, false));
		
		BasicSaveResponse response = searchSettingsController.save(model);
		
		Assert.assertEquals(response.getCode(), IWebUtilsCommonConstants.RESPONSE_CODE_SUCCESS);

		//validate save
		SearchSettingsModel savedModel = getSearchSettings("empSearch");
		Assert.assertEquals(savedModel.getSearchQueryName(), "empSearch");
		Assert.assertEquals(savedModel.getPageSize(), 20);
		Assert.assertEquals(savedModel.getSearchColumns().size(), 3);
		
		Assert.assertEquals(savedModel.getSearchColumns().get(0).getLabel(), "Name");
		Assert.assertEquals(savedModel.getSearchColumns().get(0).isDisplayed(), true);

		Assert.assertEquals(savedModel.getSearchColumns().get(1).getLabel(), "Id");
		Assert.assertEquals(savedModel.getSearchColumns().get(1).isDisplayed(), false);

		Assert.assertEquals(savedModel.getSearchColumns().get(2).getLabel(), "Salary");
		Assert.assertEquals(savedModel.getSearchColumns().get(2).isDisplayed(), false);
	}

	@AfterClass
	private void clean()
	{
		searchSettingsController.deleteAll();
	}
}
