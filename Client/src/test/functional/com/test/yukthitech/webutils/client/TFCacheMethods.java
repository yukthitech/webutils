package com.test.yukthitech.webutils.client;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.test.yukthitech.webutils.models.ITestController;

public class TFCacheMethods extends TFBase
{
	private ITestController testController;
	
	@BeforeClass
	public void setup()
	{
		testController = super.clientControllerFactory.getController(ITestController.class);
	}

	@Test(priority = 1)
	public void testBasicCaching()
	{
		Assert.assertEquals(testController.getTestBean(100).getModel().getName(), "1");
		Assert.assertEquals(testController.getTestBean(101).getModel().getName(), "2");
	
		//ensure cache is working properly
		Assert.assertEquals(testController.getTestBean(100).getModel().getName(), "1");
		
		//ensure group eviction is working properly
		Assert.assertEquals(testController.count().getModel(), new Integer(2));
		Assert.assertEquals(testController.count().getModel(), new Integer(2));
		testController.reset();
		
		Assert.assertEquals(testController.count().getModel(), new Integer(0));
	}
	
	@Test(priority = 2)
	public void testBasicEvication()
	{
		testController.reset();
		
		Assert.assertEquals(testController.getTestBean(200).getModel().getName(), "1");
		Assert.assertEquals(testController.getTestBean(201).getModel().getName(), "2");
		Assert.assertEquals(testController.getTestBean(200).getModel().getName(), "1");
		
		Assert.assertEquals(testController.count().getModel(), new Integer(2));
		//remove one key and ensure eviction is working properly
		testController.deleteBean(200);
		Assert.assertEquals(testController.count().getModel(), new Integer(1));
		
		Assert.assertEquals(testController.getTestBean(200).getModel().getName(), "3");
		Assert.assertEquals(testController.count().getModel(), new Integer(2));
	}
}
