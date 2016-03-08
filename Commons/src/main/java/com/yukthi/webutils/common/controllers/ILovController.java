package com.yukthi.webutils.common.controllers;

import com.yukthi.webutils.common.LovType;
import com.yukthi.webutils.common.RemoteService;
import com.yukthi.webutils.common.models.LovListResponse;

@RemoteService
public interface ILovController extends IClientController<ILovController>
{

	/**
	 * Service method to fetch LOV values.
	 * @param lovName LOV name whose values needs to be fetched
	 * @param type Static or dynamic lov
	 * @return Response hacing list of LOV values
	 */
	LovListResponse fetchLov(String lovName, LovType type);

}