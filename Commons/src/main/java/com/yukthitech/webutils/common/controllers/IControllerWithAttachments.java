package com.yukthitech.webutils.common.controllers;

import com.yukthitech.webutils.common.models.BaseResponse;
import com.yukthitech.webutils.common.models.BasicSaveResponse;

/**
 * Controller for entities with attachments.
 * @author akiran
 * @param <M> Model type
 * @param <R> Http multi part request
 * @param <C> Controller that supports attachments
 */
public interface IControllerWithAttachments<M, R, C extends IControllerWithAttachments<M, R, C>> extends ICrudController<M, C>
{
	/**
	 * saves the model with specified details. Saves the attachments if any (which is not done in base class).
	 *
	 * @param model the model
	 * @param request the request
	 * @return the basic save response
	 */
	public BasicSaveResponse save(M model, R request);
	
	/**
	 * Updates specified model. Saves the attachments if any (which is not done in base class).
	 *
	 * @param model the model
	 * @param request the request
	 * @return the base response
	 */
	public BaseResponse update(M model, R request);
}
