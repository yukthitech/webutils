package com.yukthitech.webutils.common.controllers;

import com.yukthitech.webutils.common.models.ModelDefResponse;

/**
 * The controller interface for fetching model definitions.
 */
public interface IModelController extends IClientController<IModelController>
{
	/**
	 * Service method to fetch model def with specified name.
	 * @param modelName Model details whose needs to be fetched
	 * @return Response model definition
	 */
	ModelDefResponse fetchModel(String modelName);
}