package com.yukthi.webutils.common.controllers;

import com.yukthi.webutils.common.RemoteService;
import com.yukthi.webutils.common.models.ModelDefResponse;

@RemoteService
public interface IModelController extends IClientController<IModelController>
{

	/**
	 * Service method to fetch LOV values
	 * @param modelName Model details whose needs to be fetched
	 * @return Response model definition
	 */
	ModelDefResponse fetchModel(String modelName);

}