package com.webutils.testapp.markdown;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webutils.common.response.BasicReadResponse;

import jakarta.validation.Valid;

/**
 * Sample + submit endpoints for markdown editor demo.
 */
@RestController
@RequestMapping("/api/testapp/markdown-demo")
public class MarkdownDemoController
{
	private static final Logger logger = LogManager.getLogger(MarkdownDemoController.class);

	public static final String SAMPLE_MARKDOWN = "# ServerMd\n\nSample markdown from **server**.";

	@GetMapping("/sample")
	public BasicReadResponse<MarkdownDemoModel> sample()
	{
		MarkdownDemoModel model = new MarkdownDemoModel();
		model.setContent(SAMPLE_MARKDOWN);
		return new BasicReadResponse<>(model);
	}

	@PostMapping("/submit")
	public BasicReadResponse<MarkdownDemoModel> submit(@RequestBody @Valid MarkdownDemoModel model)
	{
		logger.info("Markdown demo submit: contentLength={}",
				model.getContent() != null ? model.getContent().length() : 0);
		return new BasicReadResponse<>(model);
	}
}
