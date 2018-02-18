/*
 * 
 */

package com.test.yukthitech.webutils.client;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.yukthitech.webutils.common.mailtemplate.IMailTemplateConfigController;
import com.yukthitech.webutils.common.mailtemplate.MailTemplateConfiguration;
import com.yukthitech.webutils.common.models.def.FieldDef;

/**
 * Test cases for mail template configurations.
 * @author akiran
 */
public class TFMailTemplateConfig extends TFBase
{
	private static Logger logger = LogManager.getLogger(TFMailTemplateConfig.class);
	
	/**
	 * Controller proxy for accessible mail template configurations.
	 */
	private IMailTemplateConfigController mailTemplateController;
	
	/**
	 * Setup.
	 */
	@BeforeClass
	public void setup()
	{
		mailTemplateController = super.clientControllerFactory.getController(IMailTemplateConfigController.class);
	}
	
	/**
	 * Test fetch names.
	 */
	@Test
	public void testFetchNames()
	{
		List<String> names = mailTemplateController.fetchNames().getValues();
		logger.debug("Got mail template names as - {}", names);
		
		Assert.assertEquals(names, Arrays.asList("TestMail1"));
	}
	
	/**
	 * Test fetch config.
	 */
	@Test
	public void testFetchConfig()
	{
		MailTemplateConfiguration config = mailTemplateController.fetchConfiguration("TestMail1").getModel();
		
		Assert.assertNotNull(config);
		
		Set<String> fieldNames = new HashSet<>();
		
		for(MailTemplateConfiguration.Field field : config.getFields())
		{
			fieldNames.add(field.getName());
		}
		
		Assert.assertEquals(fieldNames, new TreeSet<>(Arrays.asList(
				"name", "age", "address", "departments",
				"address.city", "address.state",
				"departments[].name"
		)));
		
		logger.debug("Fields obtained are: ");
		
		for(MailTemplateConfiguration.Field field : config.getFields())
		{
			logger.debug(field.getName() + " ==> " + field.getDescription());
		}
		
		logger.debug("Attachments obtained: ");
		
		for(MailTemplateConfiguration.Attachment attachment : config.getAttachments())
		{
			logger.debug(attachment.getName() + " ==> " + attachment.getDescription() + "[Image: " + attachment.isImage() + "]");
		}
	}
}
