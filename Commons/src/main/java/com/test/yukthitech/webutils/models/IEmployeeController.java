package com.test.yukthitech.webutils.models;

import com.yukthitech.webutils.common.controllers.ICrudController;
import com.yukthitech.webutils.common.models.BaseResponse;
import com.yukthitech.webutils.common.models.BasicCountResponse;

public interface IEmployeeController extends ICrudController<TestEmployeeModel, IEmployeeController>
{
	BaseResponse deleteAll();

	BasicCountResponse count();

}