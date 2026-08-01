package com.webutils.testapp.markdown;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webutils.common.response.BasicReadResponse;

import jakarta.validation.Valid;

/**
 * Accepts markdown demo form posts and echoes the submitted model for UI assertion.
 */
@RestController
@RequestMapping("/api/testapp/markdown-demo")
public class MarkdownDemoController
{
	private static final Logger logger = LogManager.getLogger(MarkdownDemoController.class);

	@PostMapping("/submit")
	public BasicReadResponse<MarkdownDemoModel> submit(@RequestBody @Valid MarkdownDemoModel model)
	{
		logger.info("Markdown demo submit: contentLength={}",
				model.getContent() != null ? model.getContent().length() : 0);
		return new BasicReadResponse<>(model);
	}
}
