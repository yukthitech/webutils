/*
 * The MIT License (MIT)
 * Copyright (c) 2016 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.webutils.common.mail.template;

import com.webutils.common.form.annotations.IgnoreField;
import com.webutils.common.form.annotations.Model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Represents data of the mail to be sent.
 * 
 * @author akiran
 */
@Model
@Data
public class MailTemplateModel
{
	/**
	 * Name of template used for building this data object.
	 */
	@NotNull
	@Size(min = 1, max = 100)
	private String templateName;

	/**
	 * To list template of the mail.
	 */
	@Size(max = 1000)
	private String toListTemplate;

	/**
	 * CC list template of the mail.
	 */
	@Size(max = 1000)
	private String ccListTemplate;

	/**
	 * BCC list template of the mail.
	 */
	@Size(max = 1000)
	private String bccListTemplate;

	/**
	 * Subject template of the mail.
	 */
	@NotNull
	@Size(min = 1, max = 1000)
	private String subjectTemplate;

	/**
	 * Content template of the mail.
	 */
	@NotNull
	@Size(min = 1)
	private String contentTemplate;

	/**
	 * Customization object that can be use by application to set customization
	 * parameters that will be used for template processing.
	 */
	@IgnoreField
	private Object customization;
}
