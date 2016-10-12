package com.test.yukthi.webutils.client;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.test.yukthi.webutils.models.ITestController;
import com.test.yukthi.webutils.models.TestMailModel;
import com.yukthi.webutils.common.models.BaseResponse;

/**
 * Test cases related to mails.
 * @author akiran
 */
public class TFMail extends TFBase
{
	private ITestController testController;
	
	@BeforeClass
	public void setup()
	{
		this.testController = super.clientControllerFactory.getController(ITestController.class);
	}

	@Test
	public void testSendMail() throws Exception
	{
		File file1 = File.createTempFile("Test", ".txt");
		FileUtils.writeStringToFile(file1, "Some test attachment content 1");
		
		File file2 = File.createTempFile("Test", ".txt");
		FileUtils.writeStringToFile(file2, "Some test attachment content 2");
		
		//check for negative test case, where validation fails
		TestMailModel mailModel = new TestMailModel();
		mailModel.setSubject("Test subject");
		mailModel.setContent("Some test content for <B>mail</B> testing.");
		mailModel.setToId("akranthikiran@gmail.com");
		mailModel.setFromId("dev@yukthi-tech.co.in");
		mailModel.setAttachment1("Some content for attachment1");
		mailModel.setAttachment2("Some content for attachment2");
		mailModel.setAttachment3("Some content for attachment3");
		
		BaseResponse response = testController.sendMail(mailModel);
		Assert.assertEquals(response.getCode(), 0);
	}
}
