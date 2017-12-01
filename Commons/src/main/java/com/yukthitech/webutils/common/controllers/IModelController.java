package com.yukthitech.webutils.common.controllers;

import com.yukthitech.webutils.common.RemoteService;
import com.yukthitech.webutils.common.models.ModelDefResponse;

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