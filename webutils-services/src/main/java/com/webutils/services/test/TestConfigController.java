package com.webutils.services.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webutils.common.IWebUtilsConstants;
import com.webutils.common.auth.Authorization;
import com.webutils.common.response.BaseResponse;
import com.webutils.common.test.SetConfigRequest;

import jakarta.validation.Valid;

/**
 * Test-only APIs for mutating in-memory configuration. Restricted to role {@code TEST}.
 */
@RestController
@RequestMapping("/api/test")
public class TestConfigController
{
	@Autowired
	private TestConfigService testConfigService;

	@Authorization(IWebUtilsConstants.ROLE_TEST)
	@PostMapping("/config")
	public BaseResponse setConfig(@RequestBody @Valid SetConfigRequest request)
	{
		testConfigService.setConfig(request);
		return new BaseResponse();
	}
}
