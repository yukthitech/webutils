package com.test.yukthitech.webutils.client;
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



import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.test.yukthitech.webutils.models.TestCustomerModel;
import com.test.yukthitech.webutils.models.TestEmployeeModel;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.utils.rest.RestClient;
import com.yukthitech.utils.rest.RestRequest;
import com.yukthitech.utils.rest.RestResult;
import com.yukthitech.webutils.client.ActionRequestBuilder;
import com.yukthitech.webutils.client.RequestHeadersCustomizer;
import com.yukthitech.webutils.client.RestException;
import com.yukthitech.webutils.common.IWebUtilsCommonConstants;
import com.yukthitech.webutils.common.controllers.IExtensionController;
import com.yukthitech.webutils.common.extensions.ExtensionFieldType;
import com.yukthitech.webutils.common.extensions.LovOption;
import com.yukthitech.webutils.common.models.BaseResponse;
import com.yukthitech.webutils.common.models.BasicCountResponse;
import com.yukthitech.webutils.common.models.BasicReadResponse;
import com.yukthitech.webutils.common.models.BasicSaveResponse;
import com.yukthitech.webutils.common.models.ExtensionFieldModel;

/**
 * @author akiran
 *
 */
public class TFExtensionValues extends TFBase
{
	private IExtensionController extensionsController;

	private long customer1, customer2;
	
	private long addCustomer(String name)
	{
		RestRequest<?> request = ActionRequestBuilder.buildRequest(super.clientContext, "customer.save", new TestCustomerModel(name), null);
		
		RestClient client = clientContext.getRestClient();
		
		RestResult<BasicSaveResponse> result = client.invokeJsonRequest(request, BasicSaveResponse.class);
		BasicSaveResponse response = result.getValue();
		
		Assert.assertEquals(response.getCode(), IWebUtilsCommonConstants.RESPONSE_CODE_SUCCESS);
	
		return response.getId();
	}
	
	private long addEmployee(long customerId, String name, long salary, String... extendedFields)
	{
		TestEmployeeModel emp = new TestEmployeeModel(name, salary);
		
		for(int i = 0; i < extendedFields.length; i += 2)
		{
			emp.setExtendedField(extendedFields[i], extendedFields[i + 1]);
		}
		
		RestRequest<?> request = ActionRequestBuilder.buildRequest(
				clientContext.setRequestCustomizer(new RequestHeadersCustomizer(CommonUtils.<String, Object>toMap("customerId", "" + customerId))), 
				"employee.save", emp, null);
		
		RestClient client = clientContext.getRestClient();
		
		RestResult<BasicSaveResponse> result = client.invokeJsonRequest(request, BasicSaveResponse.class);
		BasicSaveResponse response = result.getValue();
		
		if(response == null || response.getCode() != IWebUtilsCommonConstants.RESPONSE_CODE_SUCCESS)
		{
			if(response != null)
			{
				throw new RestException(response.getMessage(), response.getCode());
			}
			
			throw new InvalidStateException("Unknow error occurred - {}", result);
		}
		
		Assert.assertEquals(response.getCode(), IWebUtilsCommonConstants.RESPONSE_CODE_SUCCESS);
	
		return response.getId();
	}
	
	private long updateEmployee(long customerId, long empId, int version, String name, long salary, String... extendedFields)
	{
		TestEmployeeModel emp = new TestEmployeeModel(name, salary);
		emp.setId(empId);
		emp.setVersion(version);
		
		for(int i = 0; i < extendedFields.length; i += 2)
		{
			emp.setExtendedField(extendedFields[i], extendedFields[i + 1]);
		}
		
		RestRequest<?> request = ActionRequestBuilder.buildRequest(
				clientContext.setRequestCustomizer(new RequestHeadersCustomizer(CommonUtils.<String, Object>toMap("customerId", "" + customerId))), 
				"employee.update", emp, null);
		
		RestClient client = clientContext.getRestClient();
		
		RestResult<BasicSaveResponse> result = client.invokeJsonRequest(request, BasicSaveResponse.class);
		BasicSaveResponse response = result.getValue();
		
		if(response == null || response.getCode() != IWebUtilsCommonConstants.RESPONSE_CODE_SUCCESS)
		{
			if(response != null)
			{
				throw new RestException(response.getMessage(), response.getCode());
			}
			
			throw new InvalidStateException("Unknow error occurred - {}", result);
		}

		return response.getId();
	}

	private void deleteEmployee(long customerId, long empId)
	{
		RestRequest<?> request = ActionRequestBuilder.buildRequest(
				clientContext.setRequestCustomizer(new RequestHeadersCustomizer(CommonUtils.<String, Object>toMap("customerId", "" + customerId))), 
				"employee.delete", null, CommonUtils.<String, Object>toMap("id", empId));
		
		RestClient client = clientContext.getRestClient();
		
		RestResult<BaseResponse> result = client.invokeJsonRequest(request, BaseResponse.class);
		BaseResponse response = result.getValue();
		
		if(response == null || response.getCode() != IWebUtilsCommonConstants.RESPONSE_CODE_SUCCESS)
		{
			if(response != null)
			{
				throw new RestException(response.getMessage(), response.getCode());
			}
			
			throw new InvalidStateException("Unknow error occurred - {}", result);
		}
	}

	private long getEmployeeCount()
	{
		RestRequest<?> request = ActionRequestBuilder.buildRequest(
				clientContext, 
				"employee.count", null, null);
		
		RestClient client = clientContext.getRestClient();
		
		RestResult<BasicCountResponse> result = client.invokeJsonRequest(request, BasicCountResponse.class);
		BasicCountResponse response = result.getValue();
		
		Assert.assertEquals(response.getCode(), IWebUtilsCommonConstants.RESPONSE_CODE_SUCCESS);
	
		return response.getCount();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private TestEmployeeModel getEmployee(long customerId, long empId)
	{
		RestRequest<?> request = ActionRequestBuilder.buildRequest(
				clientContext.setRequestCustomizer(new RequestHeadersCustomizer(CommonUtils.<String, Object>toMap("customerId", "" + customerId))), 
				"employee.fetch", null, CommonUtils.<String, Object>toMap(
						"id", "" + empId
					));
		
		RestClient client = clientContext.getRestClient();
		
		RestResult<BasicReadResponse<TestEmployeeModel>> result = (RestResult)client.invokeJsonRequest(request, BasicReadResponse.class, TestEmployeeModel.class);
		return result.getValue().getModel();
	}

	private long addExtensionField(long customerId, ExtensionFieldModel field)
	{
		long id = extensionsController
			.setRequestCustomizer(new RequestHeadersCustomizer(CommonUtils.<String, Object>toMap("customerId", "" + customerId)))
			.saveExtensionField(field).getId();
		
		return id;
	}

	
	/**
	 * Sets up test environment by creating customer objects and adding extended field definitions
	 */
	@BeforeClass
	private void setup()
	{
		extensionsController = super.clientControllerFactory.getController(IExtensionController.class);
		
		customer1 = addCustomer("Customer4Val1");
		customer2 = addCustomer("Customer4Val2");
		
		//add extension fields for Employee under customer 1
		addExtensionField(customer1, new ExtensionFieldModel("Employee", "field1", "field1", "Desc1", ExtensionFieldType.INTEGER, true) );
		addExtensionField(customer1, new ExtensionFieldModel("Employee", "field2", "field2", "Desc2", ExtensionFieldType.DECIMAL, false) );
		addExtensionField(customer1, new ExtensionFieldModel("Employee", "field3", "field3", "Desc3", false, 
				Arrays.asList(
						new LovOption("1", "Label1"),
						new LovOption("2", "Label2")) ) );
		
		//add extension fields for Employee under customer 2
		addExtensionField(customer2, new ExtensionFieldModel("Employee", "field1", "field1", "Desc1", ExtensionFieldType.BOOLEAN, false) );
		addExtensionField(customer2, new ExtensionFieldModel("Employee", "field2", "field2", "Desc2", ExtensionFieldType.DATE, true) );
		addExtensionField(customer2, new ExtensionFieldModel("Employee", "field3", "field3", "Desc3", ExtensionFieldType.MULTI_LINE_STRING, false, 10) );
		addExtensionField(customer2, new ExtensionFieldModel("Employee", "field4", "field4", "Desc4", ExtensionFieldType.STRING, true, 10) );
	}
	
	/**
	 * Tests if values can be added to extended fields and able to retrieve the same
	 */
	@Test
	public void testValueAddition()
	{
		
		//create employee objects with extended fields
		long id1 = addEmployee(customer1, "emp1", 100, 
				"field1", "123", "field2", "3.45", "field3", "2");
		
		long id2 = addEmployee(customer1, "emp2", 200, 
				"field1", "1234", "field2", "4");

		long id3 = addEmployee(customer2, "emp3", 300, 
				"field1", "true", "field2", "12/11/2015", "field3", "str1", "field4", "dfdf\ndffd");

		
		//fetch and validate models
		TestEmployeeModel emp1 = getEmployee(customer1, id1);
		
		Assert.assertEquals(emp1.getExtendedFields().size(), 3);
		Assert.assertEquals(emp1.getExtendedFields().get("field1"), "123");
		Assert.assertEquals(emp1.getExtendedFields().get("field2"), "3.45");
		Assert.assertEquals(emp1.getExtendedFields().get("field3"), "2");

		TestEmployeeModel emp2 = getEmployee(customer1, id2);
		
		Assert.assertEquals(emp2.getExtendedFields().get("field1"), "1234");
		Assert.assertEquals(emp2.getExtendedFields().get("field2"), "4");
		Assert.assertNull(emp2.getExtendedFields().get("field3"));

		TestEmployeeModel emp3 = getEmployee(customer2, id3);
		
		Assert.assertEquals(emp3.getExtendedFields().size(), 4);
		Assert.assertEquals(emp3.getExtendedFields().get("field1"), "true");
		Assert.assertEquals(emp3.getExtendedFields().get("field2"), "12/11/2015");
		Assert.assertEquals(emp3.getExtendedFields().get("field3"), "str1");
		Assert.assertEquals(emp3.getExtendedFields().get("field4"), "dfdf\ndffd");
	}

	/**
	 * Tests working of extended field validations during value addition (entity creation)
	 */
	@Test
	public void testValidationsDuringAddition()
	{
		long initialCount = getEmployeeCount();
		
		
		//test by passing invalid int
		try
		{
			addEmployee(customer1, "emp1", 100, 
					"field1", "123x", "field2", "3.45", "field3", "2");
			Assert.fail("No exception is thrown when invalid int is passed");
		}catch(RestException ex)
		{
			Assert.assertTrue(ex.getMessage().contains("field1"));
		}
		
		Assert.assertEquals(getEmployeeCount(), initialCount);

		//test by passing invalid decimal
		try
		{
			addEmployee(customer1, "emp1", 100, 
					"field1", "123", "field2", "3.x45", "field3", "2");
			Assert.fail("No exception is thrown when invalid decimal is passed");
		}catch(RestException ex)
		{
			Assert.assertTrue(ex.getMessage().contains("field2"));
		}
		
		Assert.assertEquals(getEmployeeCount(), initialCount);
		
		//test by passing invalid lov
		try
		{
			addEmployee(customer1, "emp1", 100, 
					"field1", "123", "field2", "3.45", "field3", "4");
			Assert.fail("No exception is thrown when invalid lov is passed");
		}catch(RestException ex)
		{
			Assert.assertTrue(ex.getMessage().contains("field3"));
		}
		
		Assert.assertEquals(getEmployeeCount(), initialCount);

		//test not passing mandatory value
		try
		{
			addEmployee(customer1, "emp1", 100, 
					"field2", "3.45", "field3", "4");
			Assert.fail("No exception is thrown when invalid lov is passed");
		}catch(RestException ex)
		{
			Assert.assertTrue(ex.getMessage().contains("field1"));
		}
		
		Assert.assertEquals(getEmployeeCount(), initialCount);
		
		//test passing invalid boolean value
		try
		{
			addEmployee(customer2, "emp3", 300, 
					"field1", "trueSD", "field2", "12/11/2015", "field3", "str1", "field4", "dfdf\ndffd");
		}catch(RestException ex)
		{
			Assert.assertTrue(ex.getMessage().contains("field1"));
		}
		
		Assert.assertEquals(getEmployeeCount(), initialCount);

		//test passing invalid date value
		try
		{
			addEmployee(customer2, "emp3", 300, 
					"field1", "false", "field2", "12/qwes/2015", "field3", "str1", "field4", "dfdf\ndffd");
		}catch(RestException ex)
		{
			Assert.assertTrue(ex.getMessage().contains("field2"));
		}
		
		Assert.assertEquals(getEmployeeCount(), initialCount);

		//test passing long string value specified
		try
		{
			addEmployee(customer2, "emp3", 300, 
					"field1", "false", "field2", "12/10/2015", "field3", "1234567890123", "field4", "dfdf\ndffd");
		}catch(RestException ex)
		{
			Assert.assertTrue(ex.getMessage().contains("field3"));
		}
		
		Assert.assertEquals(getEmployeeCount(), initialCount);

		//test passing long multi-line string value specified
		try
		{
			addEmployee(customer2, "emp3", 300, 
					"field1", "false", "field2", "12/10/2015", "field3", "12345", "field4", "1234567890123");
		}catch(RestException ex)
		{
			Assert.assertTrue(ex.getMessage().contains("field4"));
		}
		
		Assert.assertEquals(getEmployeeCount(), initialCount);
	}

	/**
	 * Tests extended value updates during update operation
	 */
	@Test
	public void testValueUpdate()
	{
		//create employee objects with extended fields
		long id1 = addEmployee(customer1, "empForUpdate", 100, 
				"field1", "123", "field2", "3.45", "field3", "2");
		
		//fetch and validate after basic save
		TestEmployeeModel emp1 = getEmployee(customer1, id1);
		
		Assert.assertEquals(emp1.getExtendedFields().size(), 3);
		Assert.assertEquals(emp1.getExtendedFields().get("field1"), "123");
		Assert.assertEquals(emp1.getExtendedFields().get("field2"), "3.45");
		Assert.assertEquals(emp1.getExtendedFields().get("field3"), "2");

		//execute update operation
		updateEmployee(customer1, id1, emp1.getVersion(), "emp1-1", 200, 
				"field1", "321", "field2", "4.35", "field3", "1");

		//validate updated values
		emp1 = getEmployee(customer1, id1);
		
		Assert.assertEquals(emp1.getExtendedFields().size(), 3);
		Assert.assertEquals(emp1.getExtendedFields().get("field1"), "321");
		Assert.assertEquals(emp1.getExtendedFields().get("field2"), "4.35");
		Assert.assertEquals(emp1.getExtendedFields().get("field3"), "1");
	}
	
	/**
	 * Tests working of extended field validations during value update (entity update)
	 */
	@Test
	public void testValidationsDuringUpdate()
	{
		
		long empId1 = addEmployee(customer1, "emp1", 100, 
				"field1", "123", "field2", "3.45", "field3", "2");
		
		TestEmployeeModel emp1 = getEmployee(customer1, empId1);

		long empId2 = addEmployee(customer2, "emp3", 300, 
				"field1", "true", "field2", "12/11/2015", "field3", "str1", "field4", "dfdf\ndffd");

		TestEmployeeModel emp2 = getEmployee(customer2, empId2);
		
		//test by passing invalid int
		try
		{
			updateEmployee(customer1, empId1, emp1.getVersion(), "emp1Updated", 100, 
					"field1", "123x", "field2", "3.45", "field3", "2");
			Assert.fail("No exception is thrown when invalid int is passed");
		}catch(RestException ex)
		{
			Assert.assertTrue(ex.getMessage().contains("field1"));
		}
		
		Assert.assertEquals(getEmployee(customer1, empId1).getName(), "emp1");

		//test by passing invalid decimal
		try
		{
			updateEmployee(customer1, empId1, emp1.getVersion(), "emp1Updated", 100, 
					"field1", "123", "field2", "3er.45", "field3", "2");
			Assert.fail("No exception is thrown when invalid decimal is passed");
		}catch(RestException ex)
		{
			Assert.assertTrue(ex.getMessage().contains("field2"));
		}
		
		Assert.assertEquals(getEmployee(customer1, empId1).getName(), "emp1");
		
		//test by passing invalid lov
		try
		{
			updateEmployee(customer1, empId1, emp1.getVersion(), "emp1Updated", 100, 
					"field1", "123", "field2", "3.45", "field3", "4");
			Assert.fail("No exception is thrown when invalid lov is passed");
		}catch(RestException ex)
		{
			Assert.assertTrue(ex.getMessage().contains("field3"));
		}
		
		Assert.assertEquals(getEmployee(customer1, empId1).getName(), "emp1");

		//test not passing mandatory value
		try
		{
			updateEmployee(customer1, empId1, emp1.getVersion(), "emp1Updated", 100, 
					"field2", "3.45", "field3", "2");
			Assert.fail("No exception is thrown when mandatroy field is missed");
		}catch(RestException ex)
		{
			Assert.assertTrue(ex.getMessage().contains("field1"));
		}
		
		Assert.assertEquals(getEmployee(customer1, empId1).getName(), "emp1");
		
		//test passing invalid boolean value
		try
		{
			updateEmployee(customer2, empId2, emp2.getVersion(), "emp3Updated", 300, 
					"field1", "trueSD", "field2", "12/11/2015", "field3", "str1", "field4", "dfdf\ndffd");
		}catch(RestException ex)
		{
			Assert.assertTrue(ex.getMessage().contains("field1"));
		}
		
		Assert.assertEquals(getEmployee(customer2, empId2).getName(), "emp3");

		//test passing invalid date value
		try
		{
			updateEmployee(customer2, empId2, emp2.getVersion(), "emp3Updated", 300, 
					"field1", "true", "field2", "12/qwes/2015", "field3", "str1", "field4", "dfdf\ndffd");
		}catch(RestException ex)
		{
			Assert.assertTrue(ex.getMessage().contains("field2"));
		}
		
		Assert.assertEquals(getEmployee(customer2, empId2).getName(), "emp3");

		//test passing long string value specified
		try
		{
			updateEmployee(customer2, empId2, emp2.getVersion(), "emp3Updated", 300, 
					"field1", "true", "field2", "12/10/2015", "field3", "1234567890123", "field4", "dfdf\ndffd");
		}catch(RestException ex)
		{
			Assert.assertTrue(ex.getMessage().contains("field3"));
		}
		
		Assert.assertEquals(getEmployee(customer2, empId2).getName(), "emp3");

		//test passing long multi-line string value specified
		try
		{
			updateEmployee(customer2, empId2, emp2.getVersion(), "emp3Updated", 300, 
					"field1", "true", "field2", "12/10/2015", "field3", "1234", "field4", "1234567890123");
		}catch(RestException ex)
		{
			Assert.assertTrue(ex.getMessage().contains("field4"));
		}
		
		Assert.assertEquals(getEmployee(customer2, empId2).getName(), "emp3");
	}

	@Test
	public void testValueDelete()
	{
		//create employee objects with extended fields
		long id1 = addEmployee(customer1, "empForUpdate", 100, 
				"field1", "123", "field2", "3.45", "field3", "2");
		
		deleteEmployee(customer1, id1);
	}
	
	@AfterClass
	private void cleanup()
	{
		//Delete customers
		RestRequest<?> request = ActionRequestBuilder.buildRequest(super.clientContext, "customer.deleteAll", null, null);
		RestClient client = clientContext.getRestClient();
		client.invokeJsonRequest(request, BaseResponse.class);

		//Delete employees
		request = ActionRequestBuilder.buildRequest(super.clientContext, "employee.deleteAll", null, null);
		client.invokeJsonRequest(request, BaseResponse.class);
		
		//delete extended fields
		extensionsController.deleteAllExtensionFields();

	}
}
