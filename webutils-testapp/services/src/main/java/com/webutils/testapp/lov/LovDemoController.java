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
 * Accepts LOV demo form posts so widgets can be exercised end-to-end without product persistence.
 */
@RestController
@RequestMapping("/api/testapp/lov-demo")
public class LovDemoController
{
	private static final Logger logger = LogManager.getLogger(LovDemoController.class);

	@PostMapping("/submit")
	public BasicReadResponse<LovDemoModel> submit(@RequestBody @Valid LovDemoModel model)
	{
		logger.info("LOV demo submit: category={}, tags={}, notes={}", model.getCategory(), model.getTags(), model.getNotes());
		return new BasicReadResponse<>(model);
	}
}
