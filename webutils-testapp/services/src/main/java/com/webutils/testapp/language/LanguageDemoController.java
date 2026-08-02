package com.webutils.testapp.language;

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
 * Sample + submit endpoints for language editor demo (JSON / XML / JSON Schema).
 */
@RestController
@RequestMapping("/api/testapp/language-demo")
public class LanguageDemoController
{
	private static final Logger logger = LogManager.getLogger(LanguageDemoController.class);

	public static final String SAMPLE_JSON = "{\n  \"autoxJson\": true,\n  \"name\": \"ServerJson\"\n}";

	public static final String SAMPLE_XML =
			"<root>\n  <autoxXml>ServerXml</autoxXml>\n</root>";

	public static final String SAMPLE_JSON_SCHEMA = "{\n"
			+ "  \"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n"
			+ "  \"type\": \"object\",\n"
			+ "  \"properties\": {\n"
			+ "    \"autoxSchema\": { \"type\": \"string\" }\n"
			+ "  },\n"
			+ "  \"required\": [\"autoxSchema\"]\n"
			+ "}";

	@GetMapping("/sample")
	public BasicReadResponse<LanguageDemoModel> sample()
	{
		LanguageDemoModel model = new LanguageDemoModel();
		model.setJsonContent(SAMPLE_JSON);
		model.setXmlContent(SAMPLE_XML);
		model.setJsonSchemaContent(SAMPLE_JSON_SCHEMA);
		return new BasicReadResponse<>(model);
	}

	@PostMapping("/submit")
	public BasicReadResponse<LanguageDemoModel> submit(@RequestBody @Valid LanguageDemoModel model)
	{
		logger.info("Language demo submit: jsonLen={}, xmlLen={}, schemaLen={}",
				len(model.getJsonContent()), len(model.getXmlContent()), len(model.getJsonSchemaContent()));
		return new BasicReadResponse<>(model);
	}

	private static int len(String value)
	{
		return value != null ? value.length() : 0;
	}
}
