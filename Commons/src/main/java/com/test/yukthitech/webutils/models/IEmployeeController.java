package com.test.yukthitech.webutils.models;

import com.yukthitech.webutils.common.RemoteService;
import com.yukthitech.webutils.common.controllers.IClientController;
import com.yukthitech.webutils.common.controllers.ICrudController;
import com.yukthitech.webutils.common.models.BaseResponse;
import com.yukthitech.webutils.common.models.BasicCountResponse;

@RemoteService
public interface IEmployeeController extends IClientController<IEmployeeController>, ICrudController<TestEmployeeModel>
{
	BaseResponse deleteAll();

	BasicCountResponse count();

}