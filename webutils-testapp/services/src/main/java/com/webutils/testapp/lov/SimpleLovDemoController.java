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
 * Accepts simple LOV demo form posts and persists selected category label into TEMP_TABLE.
 */
@RestController
@RequestMapping("/api/testapp/simple-lov-demo")
public class SimpleLovDemoController
{
	private static final Logger logger = LogManager.getLogger(SimpleLovDemoController.class);

	@Autowired
	private SimpleLovDemoService simpleLovDemoService;

	@PostMapping("/submit")
	public BasicReadResponse<TempTableEntity> submit(@RequestBody @Valid SimpleLovDemoModel model)
	{
		logger.info("Simple LOV demo submit: categoryId={}", model.getCategoryId());
		TempTableEntity saved = simpleLovDemoService.submit(model);
		return new BasicReadResponse<>(saved);
	}
}
