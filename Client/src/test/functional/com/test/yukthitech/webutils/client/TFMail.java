package com.test.yukthitech.webutils.client;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.test.yukthitech.webutils.models.ITestController;
import com.test.yukthitech.webutils.models.TestMailModel;
import com.yukthitech.webutils.common.models.BaseResponse;
import com.yukthitech.webutils.common.models.BasicReadListResponse;

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
		mailModel.setToId("akiran@yodlee.com");
		mailModel.setFromId("dev@yukthi-tech.co.in");
		mailModel.setAttachment1("Some content for attachment1");
		mailModel.setAttachment2("Some content for attachment2");
		mailModel.setAttachment3("Some content for attachment3");
		
		BaseResponse response = testController.sendMail(mailModel);
		Assert.assertEquals(response.getCode(), 0);
	}
	
	@Test
	public void readMails() throws Exception
	{
		BasicReadListResponse<TestMailModel> mails = testController.readMails();
		
		if(mails.getValues().isEmpty())
		{
			System.out.println("No mails found in inbox.");
			return;
		}
		
		for(TestMailModel mail : mails.getValues())
		{
			System.out.println("\n\n*******************************************");
			System.out.println("\nFrom: " + mail.getFromId());
			System.out.println("\nSubject: " + mail.getSubject());
			System.out.println("\nContent: " + mail.getContent());
		}
	}
}
