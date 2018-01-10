package com.test.yukthitech.webutils.models;

import com.yukthitech.webutils.common.models.BaseResponse;
import com.yukthitech.webutils.common.models.BasicReadListResponse;

public interface ITestController
{

	/**
	 * Simple test control method which is used by client test cases to 
	 * check for spring validation enabling.
	 * @param testBean
	 * @return
	 */
	BaseResponse test(TestBean testBean);

	BaseResponse secured1(TestBean testBean);

	BaseResponse secured2(TestBean testBean);

	BaseResponse sendMail(TestMailModel model) throws Exception;
	
	BasicReadListResponse<TestMailModel> readMails() throws Exception;

}