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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.test.yukthi.webutils.models.TestCustomerModel;
import com.yukthi.utils.CommonUtils;
import com.yukthi.utils.rest.RestClient;
import com.yukthi.utils.rest.RestRequest;
import com.yukthi.utils.rest.RestResult;
import com.yukthi.webutils.client.ActionRequestBuilder;
import com.yukthi.webutils.client.RequestHeadersCustomizer;
import com.yukthi.webutils.common.IWebUtilsCommonConstants;
import com.yukthi.webutils.common.controllers.IExtensionController;
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
	
	private IExtensionController extensionController;
	
	@BeforeClass
	public void setup()
	{
		extensionController = super.clientControllerFactory.getController(IExtensionController.class);
	}
	
	private long addCustomer(String name)
	{
		RestRequest<?> request = ActionRequestBuilder.buildRequest(super.clientContext, "customer.save", new TestCustomerModel(name), null);
		
		RestClient client = clientContext.getRestClient();
		
		RestResult<BasicSaveResponse> result = client.invokeJsonRequest(request, BasicSaveResponse.class);
		BasicSaveResponse response = result.getValue();
		
		Assert.assertEquals(response.getCode(), IWebUtilsCommonConstants.RESPONSE_CODE_SUCCESS);
	
		return response.getId();
	}
	
	private long saveExtensionField(String customerId, ExtensionFieldModel field)
	{
		clientContext.setRequestCustomizer(new RequestHeadersCustomizer(CommonUtils.toMap("customerId", customerId)));
		return extensionController.saveExtensionField(field).getId();
	}
	
	/**
	 * Tests when extensions are under one owner other owner extensions are not getting effected
	 */
	@Test
	public void testExtendedFieldAddition()
	{
		String id1 = "" + addCustomer("Customer1");
		String id2 = "" + addCustomer("Customer2");
		
		saveExtensionField(id1, new ExtensionFieldModel("Employee", "field1", "field1", "id1-Desc1", ExtensionFieldType.INTEGER, true) );

		saveExtensionField(id1, new ExtensionFieldModel("Employee", "field2", "field2", "id1-Desc2", ExtensionFieldType.DECIMAL, true) );

		saveExtensionField(id2, new ExtensionFieldModel("Employee", "field1", "field1", "id2-Desc1", false, 
				Arrays.asList(
						new LovOption("1", "Label1"),
						new LovOption("2", "Label2")) ) );

		List<ExtensionFieldModel> fieldList1 = extensionController
			.setRequestCustomizer(new RequestHeadersCustomizer(CommonUtils.toMap("customerId", id1)))
			.fetchExtensionFields("Employee").getExtensionFields();
		
		Assert.assertEquals(fieldList1.size(), 2);
		Assert.assertEquals(CommonUtils.toSet(fieldList1.get(0).getName(), fieldList1.get(1).getName()), 
				CommonUtils.toSet("field1", "field2"));
		Assert.assertEquals(CommonUtils.toSet(fieldList1.get(0).getType(), fieldList1.get(1).getType()), 
				CommonUtils.toSet(ExtensionFieldType.INTEGER, ExtensionFieldType.DECIMAL));
		Assert.assertEquals(fieldList1.get(0).isRequired(), true);
		
		List<ExtensionFieldModel> fieldList2 = extensionController
			.setRequestCustomizer(new RequestHeadersCustomizer(CommonUtils.toMap("customerId", id2)))
			.fetchExtensionFields("Employee").getExtensionFields();
		
		Assert.assertEquals(fieldList2.size(), 1);
		Assert.assertEquals(fieldList2.get(0).getName(), "field1");
		Assert.assertEquals(fieldList2.get(0).getLabel(), "field1");
		Assert.assertEquals(fieldList2.get(0).getDescription(), "id2-Desc1");
		Assert.assertEquals(fieldList2.get(0).isRequired(), false);
		Assert.assertEquals(fieldList2.get(0).getType(), ExtensionFieldType.LIST_OF_VALUES);
		Assert.assertEquals(CommonUtils.toSet(fieldList2.get(0).getLovOptions().get(0).getValue(), fieldList2.get(0).getLovOptions().get(1).getValue()), 
				CommonUtils.toSet("1", "2"));
		Assert.assertEquals(CommonUtils.toSet(fieldList2.get(0).getLovOptions().get(0).getLabel(), fieldList2.get(0).getLovOptions().get(1).getLabel()), 
				CommonUtils.toSet("Label1", "Label2"));

		try
		{
			extensionController
				.setRequestCustomizer(new RequestHeadersCustomizer(CommonUtils.toMap("customerId", id1)))
				.saveExtensionField(new ExtensionFieldModel("Employee", "field1", "field1", "Desc4", ExtensionFieldType.BOOLEAN, true) );
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
		
		ExtensionFieldModel field1 = new ExtensionFieldModel("Employee", "field1", "field1", "Desc1", ExtensionFieldType.INTEGER, true);
		ExtensionFieldModel field2 = new ExtensionFieldModel("Employee", "field2", "field2", "Desc2", ExtensionFieldType.DECIMAL, true);
		
		long fieldId1 = saveExtensionField(id1, field1 );
		saveExtensionField(id1, field2);

		List<ExtensionFieldModel> fieldList1 = extensionController
			.setRequestCustomizer(new RequestHeadersCustomizer(CommonUtils.toMap("customerId", id1)))
			.fetchExtensionFields("Employee").getExtensionFields();
		
		Assert.assertEquals(fieldList1.size(), 2);
		Assert.assertEquals(CommonUtils.toSet(fieldList1.get(0).getName(), fieldList1.get(1).getName()), 
				CommonUtils.toSet("field1", "field2"));
		Assert.assertEquals(CommonUtils.toSet(fieldList1.get(0).getType(), fieldList1.get(1).getType()), 
				CommonUtils.toSet(ExtensionFieldType.INTEGER, ExtensionFieldType.DECIMAL));
		
		
		//update and validate
		field1.setId(fieldId1);
		field1.setType(ExtensionFieldType.DATE);
		field1.setVersion(1);
		
		extensionController
			.setRequestCustomizer(new RequestHeadersCustomizer(CommonUtils.toMap("customerId", id1)))
			.updateExtensionField(field1);
		
		fieldList1 = extensionController
			.setRequestCustomizer(new RequestHeadersCustomizer(CommonUtils.toMap("customerId", id1)))
			.fetchExtensionFields("Employee").getExtensionFields();
		
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
		
		ExtensionFieldModel field1 = new ExtensionFieldModel("Employee", "field1", "field1", "Desc1", ExtensionFieldType.INTEGER, true);
		ExtensionFieldModel field2 = new ExtensionFieldModel("Employee", "field2", "field2", "Desc2", ExtensionFieldType.DECIMAL, true);
		ExtensionFieldModel field3 = new ExtensionFieldModel("Employee", "field3", "field3", "Desc3", ExtensionFieldType.BOOLEAN, true);
		
		long fieldId1 = saveExtensionField(id1, field1);

		saveExtensionField(id1, field2);
		
		saveExtensionField(id1, field3);
		
		List<ExtensionFieldModel> fieldList1 = extensionController
			.setRequestCustomizer(new RequestHeadersCustomizer(CommonUtils.toMap("customerId", id1)))
			.fetchExtensionFields("Employee").getExtensionFields();
		Assert.assertEquals(fieldList1.size(), 3);
		Assert.assertEquals(CommonUtils.toSet(fieldList1.get(0).getName(), fieldList1.get(1).getName(), fieldList1.get(2).getName()), 
				CommonUtils.toSet("field1", "field2", "field3"));
		Assert.assertEquals(CommonUtils.toSet(fieldList1.get(0).getType(), fieldList1.get(1).getType(), fieldList1.get(2).getType()), 
				CommonUtils.toSet(ExtensionFieldType.INTEGER, ExtensionFieldType.DECIMAL, ExtensionFieldType.BOOLEAN));
		
		
		//update and validate
		extensionController
			.setRequestCustomizer(new RequestHeadersCustomizer(CommonUtils.toMap("customerId", id1)))
			.deleteExtensionField("Employee", fieldId1);
		
		fieldList1 = extensionController
						.setRequestCustomizer(new RequestHeadersCustomizer(CommonUtils.toMap("customerId", id1))) 
						.fetchExtensionFields("Employee").getExtensionFields();
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
		extensionController.deleteAllExtensionFields();
		
		extensionController.saveExtensionField(  
				new ExtensionFieldModel("Customer", "fieldAdd1", "fieldAdd1", "Desc1", ExtensionFieldType.INTEGER, true));

		extensionController.saveExtensionField( 
				new ExtensionFieldModel("Customer", "fieldAdd2", "fieldAdd2", "Desc2", ExtensionFieldType.DECIMAL, true));

		List<ExtensionFieldModel> fieldList1 = extensionController.fetchExtensionFields("Customer").getExtensionFields();
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
		extensionController.deleteAllExtensionFields();
		
		ExtensionFieldModel field1 = new ExtensionFieldModel("Customer", "fieldUpd1", "fieldUpd1", "Desc1", ExtensionFieldType.INTEGER, true);
		ExtensionFieldModel field2 = new ExtensionFieldModel("Customer", "fieldUpd2", "fieldUpd2", "Desc2", ExtensionFieldType.DECIMAL, true);
		
		long fieldId1 = extensionController.saveExtensionField(field1).getId();

		extensionController.saveExtensionField(field2);

		List<ExtensionFieldModel> fieldList1 = extensionController.fetchExtensionFields("Customer").getExtensionFields();
		Assert.assertEquals(fieldList1.size(), 2);
		Assert.assertEquals(CommonUtils.toSet(fieldList1.get(0).getName(), fieldList1.get(1).getName()), 
				CommonUtils.toSet("fieldUpd1", "fieldUpd2"));
		Assert.assertEquals(CommonUtils.toSet(fieldList1.get(0).getType(), fieldList1.get(1).getType()), 
				CommonUtils.toSet(ExtensionFieldType.INTEGER, ExtensionFieldType.DECIMAL));
		
		
		//update and validate
		field1.setId(fieldId1);
		field1.setType(ExtensionFieldType.DATE);
		field1.setVersion(1);
		
		extensionController.updateExtensionField(field1);
		
		fieldList1 = extensionController.fetchExtensionFields("Customer").getExtensionFields();
		Assert.assertEquals(fieldList1.size(), 2);
		Assert.assertEquals(CommonUtils.toSet(fieldList1.get(0).getName(), fieldList1.get(1).getName()), 
				CommonUtils.toSet("fieldUpd1", "fieldUpd2"));
		Assert.assertEquals(CommonUtils.toSet(fieldList1.get(0).getType(), fieldList1.get(1).getType()), 
				CommonUtils.toSet(ExtensionFieldType.DATE, ExtensionFieldType.DECIMAL));
	}

	@Test
	public void testDeleteExtensionField_noOwner()
	{
		extensionController.deleteAllExtensionFields();
		
		ExtensionFieldModel field1 = new ExtensionFieldModel("Customer", "fieldDel1", "fieldDel1", "Desc1", ExtensionFieldType.INTEGER, true);
		ExtensionFieldModel field2 = new ExtensionFieldModel("Customer", "fieldDel2", "fieldDel2", "Desc2", ExtensionFieldType.DECIMAL, true);
		ExtensionFieldModel field3 = new ExtensionFieldModel("Customer", "fieldDel3", "fieldDel3", "Desc3", ExtensionFieldType.BOOLEAN, true);
		
		long fieldId1 = extensionController.saveExtensionField(field1).getId();

		extensionController.saveExtensionField(field2);

		extensionController.saveExtensionField(field3);

		List<ExtensionFieldModel> fieldList1 = extensionController.fetchExtensionFields("Customer").getExtensionFields();
		Assert.assertEquals(fieldList1.size(), 3);
		Assert.assertEquals(CommonUtils.toSet(fieldList1.get(0).getName(), fieldList1.get(1).getName(), fieldList1.get(2).getName()), 
				CommonUtils.toSet("fieldDel1", "fieldDel2", "fieldDel3"));
		Assert.assertEquals(CommonUtils.toSet(fieldList1.get(0).getType(), fieldList1.get(1).getType(), fieldList1.get(2).getType()), 
				CommonUtils.toSet(ExtensionFieldType.INTEGER, ExtensionFieldType.DECIMAL, ExtensionFieldType.BOOLEAN));
		
		
		//update and validate
		extensionController.deleteExtensionField("Customer", fieldId1);
		
		fieldList1 = extensionController.fetchExtensionFields("Customer").getExtensionFields();
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
		
		extensionController.deleteAllExtensionFields();
	}
}
