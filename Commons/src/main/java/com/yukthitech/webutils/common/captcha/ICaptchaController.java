package com.yukthitech.webutils.common.captcha;

import com.yukthitech.webutils.common.controllers.IClientController;

/**
 * Controller for managing captchas.
 * @author akiran
 */
public interface ICaptchaController extends IClientController<ICaptchaController>
{
	public CaptchaResponse generate() throws Exception;
}