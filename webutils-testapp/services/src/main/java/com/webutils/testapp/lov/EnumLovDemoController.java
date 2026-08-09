package com.webutils.testapp.lov;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webutils.common.response.BasicReadResponse;

import jakarta.validation.Valid;

/**
 * Echoes enum LOV demo form posts (no TEMP_TABLE persistence required).
 */
@RestController
@RequestMapping("/api/testapp/enum-lov-demo")
public class EnumLovDemoController
{
	private static final Logger logger = LogManager.getLogger(EnumLovDemoController.class);

	@PostMapping("/submit")
	public BasicReadResponse<EnumLovDemoModel> submit(@RequestBody @Valid EnumLovDemoModel model)
	{
		logger.info("Enum LOV demo submit: status={}, statuses={}", model.getStatus(), model.getStatuses());
		return new BasicReadResponse<>(model);
	}
}
