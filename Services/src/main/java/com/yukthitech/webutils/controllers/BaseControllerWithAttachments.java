package com.yukthitech.webutils.controllers;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.yukthitech.webutils.annotations.ActionName;
import com.yukthitech.webutils.annotations.AttachmentsExpected;
import com.yukthitech.webutils.common.BaseModel;
import com.yukthitech.webutils.common.IWebUtilsActionConstants;
import com.yukthitech.webutils.common.IWebUtilsCommonConstants;
import com.yukthitech.webutils.common.controllers.IControllerWithAttachments;
import com.yukthitech.webutils.common.models.BaseResponse;
import com.yukthitech.webutils.common.models.BasicSaveResponse;
import com.yukthitech.webutils.services.BaseCrudService;

/**
 * Base class for crud controllers with common functionalities.
 * @author akiran
 * @param <M> Model type for which controller is being defined.
 * @param <S> service type to be used.
 * @param <C> Current controller type.
 */
public class BaseControllerWithAttachments<M extends BaseModel, S extends BaseCrudService<?, ?>, C extends IControllerWithAttachments<M, MultipartHttpServletRequest, C>> 
		extends BaseCrudController<M, S, C>
		implements IControllerWithAttachments<M, MultipartHttpServletRequest, C>
{
	/**
	 * saves the employer with specified details. Saves the attachments if any (which is not done in base class).
	 *
	 * @param model the model
	 * @param request the request
	 * @return the basic save response
	 */
	@AttachmentsExpected
	@ActionName(IWebUtilsActionConstants.ACTION_TYPE_SAVE)
	@RequestMapping(value = "/saveCandidate", method = RequestMethod.POST)
	@ResponseBody
	public BasicSaveResponse save(@RequestPart(IWebUtilsCommonConstants.MULTIPART_DEFAULT_PART) @Valid M model, MultipartHttpServletRequest request)
	{
		return super.save(model);
	}

	/**
	 * Updates specified candidate. Saves the attachments if any (which is not done in base class).
	 *
	 * @param model the model
	 * @param request the request
	 * @return the base response
	 */
	@AttachmentsExpected
	@ActionName(IWebUtilsActionConstants.ACTION_TYPE_UPDATE)
	@RequestMapping(value = "/updateCandidate", method = RequestMethod.POST)
	@ResponseBody
	public BaseResponse update(@RequestPart(IWebUtilsCommonConstants.MULTIPART_DEFAULT_PART) @Valid M model, MultipartHttpServletRequest request)
	{
		return super.update(model);
	}
}
