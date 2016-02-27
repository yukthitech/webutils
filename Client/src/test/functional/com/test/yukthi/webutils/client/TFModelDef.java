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

package com.test.yukthi.webutils.client;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.yukthi.webutils.client.ClientControllerFactory;
import com.yukthi.webutils.common.LovType;
import com.yukthi.webutils.common.controllers.IModelController;
import com.yukthi.webutils.common.models.def.FieldDef;
import com.yukthi.webutils.common.models.def.ModelDef;
import com.yukthi.webutils.common.models.def.ValidationDef;

/**
 * Test LOV value fetching from server
 * @author akiran
 */
public class TFModelDef extends TFBase
{
	private static Logger logger = LogManager.getLogger(TFModelDef.class);
	
	private IModelController modelController;
	
	@BeforeClass
	public void setup()
	{
		modelController = super.clientControllerFactory.getController(IModelController.class);
	}
	
	@Test
	public void testModelDef()
	{
		ModelDef modelDef = modelController.fetchModel("TestModel").getModelDef(); 
		
		logger.debug("Got model def as - " + modelDef);
		
		//ensure model basic properties are good
		Assert.assertNotNull(modelDef);
		Assert.assertEquals(modelDef.getName(), "TestModel");
		Assert.assertEquals(modelDef.getLabel(), "Test Bean");
		Assert.assertEquals(modelDef.getFields().size(), 6);
		
		int fieldMatchCount = 0, validMatchCount = 0;
		
		//ensure different expected fields are present with expected values
		for(FieldDef fieldDef : modelDef.getFields())
		{
			if("name".equals(fieldDef.getName()))
			{
				Assert.assertEquals(fieldDef.getValidations().size(), 1);
				Assert.assertEquals(fieldDef.getValidations().get(0).getName(), "required");
				fieldMatchCount++;
			}
			else if("age".equals(fieldDef.getName()))
			{
				Assert.assertEquals(fieldDef.getValidations().size(), 2);
				
				for(ValidationDef validationDef : fieldDef.getValidations())
				{
					if("minValue".equals(validationDef.getName()))
					{
						Assert.assertEquals("" + validationDef.getValues().get("value"), "18");
						validMatchCount++;
					}
					else
					{
						Assert.assertEquals("" + validationDef.getValues().get("value"), "30");
						validMatchCount++;
					}
				}

				Assert.assertEquals(validMatchCount, 2);
				fieldMatchCount++;
			}
			else if("password".equals(fieldDef.getName()))
			{
				Assert.assertEquals(fieldDef.getValidations().size(), 1);
				Assert.assertEquals(fieldDef.getLabel(), "PASS");
				Assert.assertEquals(fieldDef.getValidations().get(0).getName(), "required");
				fieldMatchCount++;
			}
			else if("lovType".equals(fieldDef.getName()))
			{
				Assert.assertTrue( CollectionUtils.isEmpty(fieldDef.getValidations()) );
				Assert.assertEquals(fieldDef.getLovDetails().getLovName(), LovType.class.getName());
				Assert.assertEquals(fieldDef.getLovDetails().getLovType(), LovType.STATIC_TYPE);
				fieldMatchCount++;
			}
			else if("empName".equals(fieldDef.getName()))
			{
				Assert.assertTrue( CollectionUtils.isEmpty(fieldDef.getValidations()) );
				Assert.assertEquals(fieldDef.getLovDetails().getLovName(), "employeeLov");
				Assert.assertEquals(fieldDef.getLovDetails().getLovType(), LovType.DYNAMIC_TYPE);
				fieldMatchCount++;
			}
		}
		
		Assert.assertEquals(fieldMatchCount, 5);
	}
}
