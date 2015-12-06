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

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.test.yukthi.webutils.models.CustomerModel;
import com.yukthi.utils.CommonUtils;
import com.yukthi.utils.rest.RestClient;
import com.yukthi.utils.rest.RestRequest;
import com.yukthi.utils.rest.RestResult;
import com.yukthi.webutils.client.ActionRequestBuilder;
import com.yukthi.webutils.client.RequestHeadersCustomizer;
import com.yukthi.webutils.client.helpers.ExtensionsHelper;
import com.yukthi.webutils.common.IWebUtilsCommonConstants;
import com.yukthi.webutils.common.extensions.ExtensionFieldType;
import com.yukthi.webutils.common.extensions.LovOption;
import com.yukthi.webutils.common.models.BasicSaveResponse;
import com.yukthi.webutils.common.models.ExtensionFieldModel;

/**
 * Test LOV value fetching from server
 * @author akiran
 */
public class TFExtensions extends TFBase
{
	private static Logger logger = LogManager.getLogger(TFExtensions.class);
	
	private ExtensionsHelper extensionsHelper = new ExtensionsHelper();
	
	private long addCustomer(String name)
	{
		RestRequest<?> request = ActionRequestBuilder.buildRequest(super.clientContext, "customer.save", new CustomerModel(name), null);
		
		RestClient client = clientContext.getRestClient();
		
		RestResult<BasicSaveResponse> result = client.invokeJsonRequest(request, BasicSaveResponse.class);
		BasicSaveResponse response = result.getValue();
		
		Assert.assertEquals(response.getCode(), IWebUtilsCommonConstants.RESPONSE_CODE_SUCCESS);
	
		return response.getId();
	}
	
	private long addExtensionField(String customerId, ExtensionFieldModel field)
	{
		return extensionsHelper.addExtensionField(
				clientContext.setRequestCustomizer(new RequestHeadersCustomizer(CommonUtils.toMap("customerId", customerId))), 
				field );
	}
	
	/**
	 * Tests when extensions are under one owner other owner extensions are not getting effected
	 */
	@Test
	public void testExtendedFieldAddition()
	{
		String id1 = "" + addCustomer("Customer1");
		String id2 = "" + addCustomer("Customer2");
		
		addExtensionField(id1, new ExtensionFieldModel("Employee", "field1", "Desc1", ExtensionFieldType.INTEGER, true) );

		addExtensionField(id1, new ExtensionFieldModel("Employee", "field2", "Desc2", ExtensionFieldType.DECIMAL, true) );

		addExtensionField(id2, new ExtensionFieldModel("Employee", "field1", "Desc3", false, 
				Arrays.asList(
						new LovOption("1", "Label1"),
						new LovOption("2", "Label2")) ) );

		List<ExtensionFieldModel> fieldList1 = extensionsHelper.fetchExtensionFields(
				clientContext.setRequestCustomizer(new RequestHeadersCustomizer(CommonUtils.toMap("customerId", id1))), "Employee");
		Assert.assertEquals(fieldList1.size(), 2);
		Assert.assertEquals(CommonUtils.toSet(fieldList1.get(0).getName(), fieldList1.get(1).getName()), 
				CommonUtils.toSet("field1", "field2"));
		Assert.assertEquals(CommonUtils.toSet(fieldList1.get(0).getType(), fieldList1.get(1).getType()), 
				CommonUtils.toSet(ExtensionFieldType.INTEGER, ExtensionFieldType.DECIMAL));
		Assert.assertEquals(fieldList1.get(0).isRequired(), true);
		
		List<ExtensionFieldModel> fieldList2 = extensionsHelper.fetchExtensionFields(
				clientContext.setRequestCustomizer(new RequestHeadersCustomizer(CommonUtils.toMap("customerId", id2))), "Employee");
		Assert.assertEquals(fieldList2.size(), 1);
		Assert.assertEquals(fieldList2.get(0).getName(), "field1");
		Assert.assertEquals(fieldList2.get(0).getDescription(), "Desc3");
		Assert.assertEquals(fieldList2.get(0).isRequired(), false);
		Assert.assertEquals(fieldList2.get(0).getType(), ExtensionFieldType.LIST_OF_VALUES);
		Assert.assertEquals(CommonUtils.toSet(fieldList2.get(0).getLovOptions().get(0).getValue(), fieldList2.get(0).getLovOptions().get(1).getValue()), 
				CommonUtils.toSet("1", "2"));
		Assert.assertEquals(CommonUtils.toSet(fieldList2.get(0).getLovOptions().get(0).getLabel(), fieldList2.get(0).getLovOptions().get(1).getLabel()), 
				CommonUtils.toSet("Label1", "Label2"));

		try
		{
			extensionsHelper.addExtensionField(
					clientContext.setRequestCustomizer(new RequestHeadersCustomizer(CommonUtils.toMap("customerId", id1))), 
					new ExtensionFieldModel("Employee", "field1", "Desc4", ExtensionFieldType.BOOLEAN, true) );
			Assert.fail("Able to add multiple fields with same name");
		}catch(Exception ex)
		{
			logger.info("Error occurred while adding duplicate field - " + ex);
			//ignore
		}
	}
	
	/**
	 * Ensures updates are happening properly
	 */
	@Test
	public void testUpdateExtensionField()
	{
		String id1 = "" + addCustomer("CustomerForUpdate");
		
		ExtensionFieldModel field1 = new ExtensionFieldModel("Employee", "field1", "Desc1", ExtensionFieldType.INTEGER, true);
		ExtensionFieldModel field2 = new ExtensionFieldModel("Employee", "field2", "Desc2", ExtensionFieldType.DECIMAL, true);
		
		long fieldId1 = addExtensionField(id1, field1 );
		addExtensionField(id1, field2);

		List<ExtensionFieldModel> fieldList1 = extensionsHelper.fetchExtensionFields(
				clientContext.setRequestCustomizer(new RequestHeadersCustomizer(CommonUtils.toMap("customerId", id1))), "Employee");
		Assert.assertEquals(fieldList1.size(), 2);
		Assert.assertEquals(CommonUtils.toSet(fieldList1.get(0).getName(), fieldList1.get(1).getName()), 
				CommonUtils.toSet("field1", "field2"));
		Assert.assertEquals(CommonUtils.toSet(fieldList1.get(0).getType(), fieldList1.get(1).getType()), 
				CommonUtils.toSet(ExtensionFieldType.INTEGER, ExtensionFieldType.DECIMAL));
		
		
		//update and validate
		field1.setId(fieldId1);
		field1.setType(ExtensionFieldType.DATE);
		field1.setVersion(1);
		
		extensionsHelper.updateExtensionField(
				clientContext.setRequestCustomizer(new RequestHeadersCustomizer(CommonUtils.toMap("customerId", id1))), 
				field1);
		
		fieldList1 = extensionsHelper.fetchExtensionFields(
				clientContext.setRequestCustomizer(new RequestHeadersCustomizer(CommonUtils.toMap("customerId", id1))), "Employee");
		Assert.assertEquals(fieldList1.size(), 2);
		Assert.assertEquals(CommonUtils.toSet(fieldList1.get(0).getName(), fieldList1.get(1).getName()), 
				CommonUtils.toSet("field1", "field2"));
		Assert.assertEquals(CommonUtils.toSet(fieldList1.get(0).getType(), fieldList1.get(1).getType()), 
				CommonUtils.toSet(ExtensionFieldType.DATE, ExtensionFieldType.DECIMAL));
	}

	@Test
	public void testDeleteExtensionField()
	{
		String id1 = "" + addCustomer("CustomerForDelete");
		
		ExtensionFieldModel field1 = new ExtensionFieldModel("Employee", "field1", "Desc1", ExtensionFieldType.INTEGER, true);
		ExtensionFieldModel field2 = new ExtensionFieldModel("Employee", "field2", "Desc2", ExtensionFieldType.DECIMAL, true);
		ExtensionFieldModel field3 = new ExtensionFieldModel("Employee", "field3", "Desc3", ExtensionFieldType.BOOLEAN, true);
		
		long fieldId1 = addExtensionField(id1, field1);

		addExtensionField(id1, field2);
		
		addExtensionField(id1, field3);
		
		List<ExtensionFieldModel> fieldList1 = extensionsHelper.fetchExtensionFields(
				clientContext.setRequestCustomizer(new RequestHeadersCustomizer(CommonUtils.toMap("customerId", id1))), 
				"Employee");
		Assert.assertEquals(fieldList1.size(), 3);
		Assert.assertEquals(CommonUtils.toSet(fieldList1.get(0).getName(), fieldList1.get(1).getName(), fieldList1.get(2).getName()), 
				CommonUtils.toSet("field1", "field2", "field3"));
		Assert.assertEquals(CommonUtils.toSet(fieldList1.get(0).getType(), fieldList1.get(1).getType(), fieldList1.get(2).getType()), 
				CommonUtils.toSet(ExtensionFieldType.INTEGER, ExtensionFieldType.DECIMAL, ExtensionFieldType.BOOLEAN));
		
		
		//update and validate
		extensionsHelper.deleteExtensionField(
				clientContext.setRequestCustomizer(new RequestHeadersCustomizer(CommonUtils.toMap("customerId", id1))), 
				"Employee", fieldId1);
		
		fieldList1 = extensionsHelper.fetchExtensionFields(
				clientContext.setRequestCustomizer(new RequestHeadersCustomizer(CommonUtils.toMap("customerId", id1))), 
				"Employee");
		Assert.assertEquals(fieldList1.size(), 2);
		Assert.assertEquals(CommonUtils.toSet(fieldList1.get(0).getName(), fieldList1.get(1).getName()), 
				CommonUtils.toSet("field2", "field3"));
		Assert.assertEquals(CommonUtils.toSet(fieldList1.get(0).getType(), fieldList1.get(1).getType()), 
				CommonUtils.toSet(ExtensionFieldType.BOOLEAN, ExtensionFieldType.DECIMAL));
	}

	/**
	 * Tests when extensions are under one owner other owner extensions are not getting effected
	 */
	@Test
	public void testExtendedFieldAddition_noOwner()
	{
		extensionsHelper.deleteAllExtensionFields(clientContext);
		
		extensionsHelper.addExtensionField(clientContext,  
				new ExtensionFieldModel("Customer", "fieldAdd1", "Desc1", ExtensionFieldType.INTEGER, true));

		extensionsHelper.addExtensionField(clientContext, 
				new ExtensionFieldModel("Customer", "fieldAdd2", "Desc2", ExtensionFieldType.DECIMAL, true));

		List<ExtensionFieldModel> fieldList1 = extensionsHelper.fetchExtensionFields(clientContext, "Customer");
		Assert.assertEquals(fieldList1.size(), 2);
		Assert.assertEquals(CommonUtils.toSet(fieldList1.get(0).getName(), fieldList1.get(1).getName()), 
				CommonUtils.toSet("fieldAdd1", "fieldAdd2"));
		Assert.assertEquals(CommonUtils.toSet(fieldList1.get(0).getType(), fieldList1.get(1).getType()), 
				CommonUtils.toSet(ExtensionFieldType.INTEGER, ExtensionFieldType.DECIMAL));
		Assert.assertEquals(fieldList1.get(0).isRequired(), true);
	}
	
	/**
	 * Ensures updates are happening properly
	 */
	@Test
	public void testUpdateExtensionField_noOwner()
	{
		extensionsHelper.deleteAllExtensionFields(clientContext);
		
		ExtensionFieldModel field1 = new ExtensionFieldModel("Customer", "fieldUpd1", "Desc1", ExtensionFieldType.INTEGER, true);
		ExtensionFieldModel field2 = new ExtensionFieldModel("Customer", "fieldUpd2", "Desc2", ExtensionFieldType.DECIMAL, true);
		
		long fieldId1 = extensionsHelper.addExtensionField(clientContext, field1);

		extensionsHelper.addExtensionField(clientContext, field2);

		List<ExtensionFieldModel> fieldList1 = extensionsHelper.fetchExtensionFields(clientContext, "Customer");
		Assert.assertEquals(fieldList1.size(), 2);
		Assert.assertEquals(CommonUtils.toSet(fieldList1.get(0).getName(), fieldList1.get(1).getName()), 
				CommonUtils.toSet("fieldUpd1", "fieldUpd2"));
		Assert.assertEquals(CommonUtils.toSet(fieldList1.get(0).getType(), fieldList1.get(1).getType()), 
				CommonUtils.toSet(ExtensionFieldType.INTEGER, ExtensionFieldType.DECIMAL));
		
		
		//update and validate
		field1.setId(fieldId1);
		field1.setType(ExtensionFieldType.DATE);
		field1.setVersion(1);
		
		extensionsHelper.updateExtensionField(clientContext, field1);
		
		fieldList1 = extensionsHelper.fetchExtensionFields(clientContext, "Customer");
		Assert.assertEquals(fieldList1.size(), 2);
		Assert.assertEquals(CommonUtils.toSet(fieldList1.get(0).getName(), fieldList1.get(1).getName()), 
				CommonUtils.toSet("fieldUpd1", "fieldUpd2"));
		Assert.assertEquals(CommonUtils.toSet(fieldList1.get(0).getType(), fieldList1.get(1).getType()), 
				CommonUtils.toSet(ExtensionFieldType.DATE, ExtensionFieldType.DECIMAL));
	}

	@Test
	public void testDeleteExtensionField_noOwner()
	{
		extensionsHelper.deleteAllExtensionFields(clientContext);
		
		ExtensionFieldModel field1 = new ExtensionFieldModel("Customer", "fieldDel1", "Desc1", ExtensionFieldType.INTEGER, true);
		ExtensionFieldModel field2 = new ExtensionFieldModel("Customer", "fieldDel2", "Desc2", ExtensionFieldType.DECIMAL, true);
		ExtensionFieldModel field3 = new ExtensionFieldModel("Customer", "fieldDel3", "Desc3", ExtensionFieldType.BOOLEAN, true);
		
		long fieldId1 = extensionsHelper.addExtensionField(clientContext, field1);

		extensionsHelper.addExtensionField(clientContext, field2);

		extensionsHelper.addExtensionField(clientContext, field3);

		List<ExtensionFieldModel> fieldList1 = extensionsHelper.fetchExtensionFields(clientContext, "Customer");
		Assert.assertEquals(fieldList1.size(), 3);
		Assert.assertEquals(CommonUtils.toSet(fieldList1.get(0).getName(), fieldList1.get(1).getName(), fieldList1.get(2).getName()), 
				CommonUtils.toSet("fieldDel1", "fieldDel2", "fieldDel3"));
		Assert.assertEquals(CommonUtils.toSet(fieldList1.get(0).getType(), fieldList1.get(1).getType(), fieldList1.get(2).getType()), 
				CommonUtils.toSet(ExtensionFieldType.INTEGER, ExtensionFieldType.DECIMAL, ExtensionFieldType.BOOLEAN));
		
		
		//update and validate
		extensionsHelper.deleteExtensionField(clientContext, "Customer", fieldId1);
		
		fieldList1 = extensionsHelper.fetchExtensionFields(clientContext, "Customer");
		Assert.assertEquals(fieldList1.size(), 2);
		Assert.assertEquals(CommonUtils.toSet(fieldList1.get(0).getName(), fieldList1.get(1).getName()), 
				CommonUtils.toSet("fieldDel2", "fieldDel3"));
		Assert.assertEquals(CommonUtils.toSet(fieldList1.get(0).getType(), fieldList1.get(1).getType()), 
				CommonUtils.toSet(ExtensionFieldType.BOOLEAN, ExtensionFieldType.DECIMAL));
	}

	@AfterClass
	public void cleanup()
	{
		RestRequest<?> request = ActionRequestBuilder.buildRequest(super.clientContext, "customer.deleteAll", null, null);
		
		RestClient client = clientContext.getRestClient();
		
		client.invokeJsonRequest(request, BasicSaveResponse.class);
		
		extensionsHelper.deleteAllExtensionFields(clientContext);
	}
}
