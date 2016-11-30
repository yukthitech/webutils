package com.yukthi.webutils.services.freemarker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test cases for free marker service.
 * @author akiran
 */
public class TFreeMarkerService
{
	private static Logger logger = LogManager.getLogger(TFreeMarkerService.class);
	
	/**
	 * Service to test.
	 */
	private FreeMarkerService freeMarkerService = new FreeMarkerService();
	
	/**
	 * Test with no args method.
	 */
	@Test
	public void testNoArgs() throws Exception
	{
		freeMarkerService.registerMethod( UtilsFunctions.class.getMethod("random") );
		
		String res = freeMarkerService.processTemplate("test", "Random value is ${random()}", new HashMap<>());
		logger.debug(res);
		
		Assert.assertTrue( res.length() > 0 );
	}

	/**
	 * Tests method with arguments.
	 */
	@Test
	public void testWithArgs() throws Exception
	{
		freeMarkerService.registerMethod( UtilsFunctions.class.getMethod("now", String.class) );
		
		String res = freeMarkerService.processTemplate("test", "Date is ${now('dd/MM/yyyy')}", new HashMap<>());
		logger.debug(res);
		
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		
		Assert.assertEquals(res, "Date is " + format.format(new Date()));
	}

	/**
	 * Tests method with arguments and var arguments.
	 */
	@Test
	public void testWithArgsAndVarArgs() throws Exception
	{
		freeMarkerService.registerMethod( UtilsFunctions.class.getMethod("minimum", double.class, double[].class) );
		
		String res = freeMarkerService.processTemplate("test", "Min is ${min(2.3, 4.5, 2, 1.2, 34)}", new HashMap<>());
		logger.debug(res);
		Assert.assertEquals(res, "Min is 1.2");

		res = freeMarkerService.processTemplate("test", "Min is ${min(2.3)}", new HashMap<>());
		logger.debug(res);
		Assert.assertEquals(res, "Min is 2.3");
	}
	
	/**
	 * Tests method with var arguments only.
	 */
	@Test
	public void testWithVarArgsOnly() throws Exception
	{
		freeMarkerService.registerMethod( UtilsFunctions.class.getMethod("sum", int[].class) );
		
		String res = freeMarkerService.processTemplate("test", "Sum is ${sum(2, 3, 4)}", new HashMap<>());
		logger.debug(res);
		Assert.assertEquals(res, "Sum is 9");

		res = freeMarkerService.processTemplate("test", "Sum is ${sum()}", new HashMap<>());
		logger.debug(res);
		Assert.assertEquals(res, "Sum is 0");
	}
}
