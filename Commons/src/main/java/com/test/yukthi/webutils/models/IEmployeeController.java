package com.test.yukthi.webutils.models;

import com.yukthi.webutils.common.RemoteService;
import com.yukthi.webutils.common.controllers.IClientController;
import com.yukthi.webutils.common.models.BaseResponse;
import com.yukthi.webutils.common.models.BasicCountResponse;
import com.yukthi.webutils.common.models.BasicReadResponse;
import com.yukthi.webutils.common.models.BasicSaveResponse;

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