package com.webutils.testapp.language;

import com.webutils.common.form.annotations.Label;
import com.webutils.common.form.annotations.Language;
import com.webutils.common.form.annotations.LanguageType;
import com.webutils.common.form.annotations.Model;
import com.yukthitech.validation.annotations.MaxLen;
import com.yukthitech.validation.annotations.Required;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Demo model for language editor widget ({@code yk-language-editor} via {@link Language}).
 */
@Data
@NoArgsConstructor
@Model(name = "LanguageDemoModel")
public class LanguageDemoModel
{
	@Label("JSON")
	@Language(LanguageType.JSON)
	@Required
	@MaxLen(10000)
	private String jsonContent;

	@Label("XML")
	@Language(LanguageType.XML)
	@Required
	@MaxLen(10000)
	private String xmlContent;

	@Label("JSON Schema")
	@Language(LanguageType.JSON_SCHEMA)
	@Required
	@MaxLen(10000)
	private String jsonSchemaContent;
}
