package com.yukthitech.webutils.captcha;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.yukthitech.webutils.WebutilsOperation;
import com.yukthitech.webutils.annotations.NoAuthentication;
import com.yukthitech.webutils.common.captcha.CaptchaResponse;
import com.yukthitech.webutils.security.ISecurityService;
import com.yukthitech.webutils.security.UnauthorizedException;

/**
 * Controller to managing captcha.
 * 
 * @author akiran
 */
@RestController
@RequestMapping("/captcha")
public class CaptchaController
{
	@Autowired
	private CaptchaService captchaService;
	
	@Autowired
	private ISecurityService securityService;
	
	@NoAuthentication
	@ResponseBody
	@RequestMapping(method = RequestMethod.GET)
	public CaptchaResponse generate() throws Exception
	{
		if(!securityService.isAuthorized(WebutilsOperation.CAPTCHA_GENERATION))
		{
			throw new UnauthorizedException("Current user is not authorized to perform this operation");
		}
		
		return captchaService.generate();
	}
}
