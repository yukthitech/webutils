package com.yukthitech.webutils.common.controllers;

import com.yukthitech.webutils.common.models.BaseResponse;
import com.yukthitech.webutils.common.models.BasicReadResponse;
import com.yukthitech.webutils.common.models.BasicSaveResponse;

/**
 * Base interface for crud controllers which provides most common apis.
 * @author akiran
 * @param <M> Model to be used by this controller
 * @param <C> Current controller type
 */
public interface ICrudController<M, C extends IClientController<C>> extends IClientController<C>
{
	/**
	 * Saves the specified model into persistence.
	 * @param model model to be saved.
	 * @return save response along with id of newly saved model.
	 */
	public BasicSaveResponse save(M model);
	
	/**
	 * Fetches model based on id.
	 * @param id id of model to be fetched.
	 * @return matching model.
	 */
	public BasicReadResponse<M> fetchById(long id);
	
	/**
	 * Updates the specified model. Model must have id of the entity to update.
	 * @param model model to be updated along with id.
	 * @return success or failure status.
	 */
	public BaseResponse update(M model);
	
	/**
	 * Deletes entity with specified id.
	 * @param id id of entity to delete.
	 * @return success or failure status.
	 */
	public BaseResponse delete(long id);
}
