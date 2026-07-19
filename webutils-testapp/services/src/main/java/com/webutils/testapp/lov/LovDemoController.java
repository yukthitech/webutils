package com.webutils.testapp.lov;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webutils.common.response.BasicReadResponse;

import jakarta.validation.Valid;

/**
 * Accepts LOV demo form posts and persists remapped category into TEMP_TABLE.
 */
@RestController
@RequestMapping("/api/testapp/lov-demo")
public class LovDemoController
{
	private static final Logger logger = LogManager.getLogger(LovDemoController.class);

	@Autowired
	private LovDemoService lovDemoService;

	@PostMapping("/submit")
	public BasicReadResponse<TempTableEntity> submit(@RequestBody @Valid LovDemoModel model)
	{
		logger.info("LOV demo submit: category={}", model.getCategory());
		TempTableEntity saved = lovDemoService.submit(model);
		return new BasicReadResponse<>(saved);
	}
}
