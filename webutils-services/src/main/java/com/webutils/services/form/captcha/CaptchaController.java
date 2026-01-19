package com.webutils.services.form.captcha;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.webutils.common.form.captcha.CaptchaResponse;
import com.webutils.services.common.NoAuthentication;

/**
 * Controller to managing captcha.
 * 
 * @author akiran
 */
@RestController
@RequestMapping("/api/v1/captcha")
public class CaptchaController
{
	@Autowired
	private CaptchaService captchaService;
	
	@NoAuthentication
	@ResponseBody
	@RequestMapping(method = RequestMethod.GET)
	public CaptchaResponse generate() throws Exception
	{
		return captchaService.generate();
	}
}
