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
 * Accepts multi-editable LOV demo form posts and persists remapped categories into TEMP_TABLE.
 */
@RestController
@RequestMapping("/api/testapp/multi-editable-lov-demo")
public class MultiEditableLovDemoController
{
	private static final Logger logger = LogManager.getLogger(MultiEditableLovDemoController.class);

	@Autowired
	private MultiEditableLovDemoService multiEditableLovDemoService;

	@PostMapping("/submit")
	public BasicReadResponse<TempTableEntity> submit(@RequestBody @Valid MultiEditableLovDemoModel model)
	{
		logger.info("Multi editable LOV demo submit: categories={}", model.getCategories());
		TempTableEntity saved = multiEditableLovDemoService.submit(model);
		return new BasicReadResponse<>(saved);
	}
}
