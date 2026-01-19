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

package com.webutils.services.mail.template;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.yukthitech.persistence.annotations.DataType;
import com.yukthitech.persistence.annotations.DataTypeMapping;
import com.yukthitech.persistence.annotations.UniqueConstraint;
import com.yukthitech.persistence.annotations.UniqueConstraints;
import com.yukthitech.persistence.conversion.impl.JsonWithTypeConverter;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Represents data of the mail to be sent.
 * 
 * @author akiran
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@UniqueConstraints({
	@UniqueConstraint(name = "TEMPLATE_NAME", fields = {"templateName", "customSpace"}, finalName = false)
	})
@Table(name = "WEBUTILS_MAIL_TEMPLATE")
public class MailTemplateEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    /**
	 * Name of template used for building this data object.
	 */
	@Column(name = "NAME", length = 100, nullable = false)
	private String templateName;
	
	@Column(name = "CUSTOM_SPACE", length = 100, nullable = false)
	private String customSpace = "";

	/**
	 * To list template of the mail.
	 */
	@Column(name = "TO_TEMPLATE", length = 1000)
	private String toListTemplate;

	/**
	 * CC list template of the mail.
	 */
	@Column(name = "CC_TEMPLATE", length = 1000)
	private String ccListTemplate;

	/**
	 * BCC list template of the mail.
	 */
	@Column(name = "BCC_TEMPLATE", length = 1000)
	private String bccListTemplate;

	/**
	 * Subject template of the mail.
	 */
	@Column(name = "SUBJECT_TEMPLATE", length = 2000, nullable = false)
	private String subjectTemplate;

	/**
	 * Content template of the mail.
	 */
	@Column(name = "CONTENT_TEMPLATE", nullable = false)
	@DataTypeMapping(type = DataType.CLOB)
	private String contentTemplate;

	/**
	 * Customization object that can be use by application to set customization
	 * parameters that will be used for template processing.
	 */
	@DataTypeMapping(type = DataType.CLOB, converterType = JsonWithTypeConverter.class)
	private Object customization;
}
