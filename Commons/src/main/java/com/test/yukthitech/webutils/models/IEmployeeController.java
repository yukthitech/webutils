package com.test.yukthitech.webutils.models;

import com.yukthitech.webutils.common.RemoteService;
import com.yukthitech.webutils.common.controllers.IClientController;
import com.yukthitech.webutils.common.models.BaseResponse;
import com.yukthitech.webutils.common.models.BasicCountResponse;
import com.yukthitech.webutils.common.models.BasicReadResponse;
import com.yukthitech.webutils.common.models.BasicSaveResponse;

@RemoteService
public interface IEmployeeController extends IClientController<IEmployeeController>
{

	BasicSaveResponse save(TestEmployeeModel model);

	BasicSaveResponse update(TestEmployeeModel model);

	BasicReadResponse<TestEmployeeModel> fetch(long id);

	BaseResponse delete(long id);

	BaseResponse deleteAll();

	BasicCountResponse count();

}