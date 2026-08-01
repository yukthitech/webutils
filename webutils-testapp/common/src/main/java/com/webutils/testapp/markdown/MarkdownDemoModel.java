package com.webutils.testapp.markdown;

import com.webutils.common.form.annotations.Label;
import com.webutils.common.form.annotations.Markdown;
import com.webutils.common.form.annotations.Model;
import com.yukthitech.validation.annotations.MaxLen;
import com.yukthitech.validation.annotations.Required;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Demo model for markdown editor widget ({@code yk-markdown-editor} via {@link Markdown}).
 */
@Data
@NoArgsConstructor
@Model(name = "MarkdownDemoModel")
public class MarkdownDemoModel
{
	@Label("Content")
	@Markdown
	@Required
	@MaxLen(10000)
	private String content;
}
