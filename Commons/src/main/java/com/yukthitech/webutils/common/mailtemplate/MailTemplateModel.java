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

package com.yukthitech.webutils.common.mailtemplate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.yukthitech.webutils.common.BaseModel;
import com.yukthitech.webutils.common.annotations.IgnoreField;
import com.yukthitech.webutils.common.annotations.Model;

/**
 * Represents data of the mail to be sent.
 * 
 * @author akiran
 */
@Model
public class MailTemplateModel extends BaseModel
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

	/**
	 * Gets the name of template used for building this data object.
	 *
	 * @return the name of template used for building this data object
	 */
	public String getTemplateName()
	{
		return templateName;
	}

	/**
	 * Sets the name of template used for building this data object.
	 *
	 * @param templateName the new name of template used for building this data object
	 */
	public void setTemplateName(String templateName)
	{
		this.templateName = templateName;
	}

	/**
	 * Gets the to list template of the mail.
	 *
	 * @return the to list template of the mail
	 */
	public String getToListTemplate()
	{
		return toListTemplate;
	}

	/**
	 * Sets the to list template of the mail.
	 *
	 * @param toListTemplate the new to list template of the mail
	 */
	public void setToListTemplate(String toListTemplate)
	{
		this.toListTemplate = toListTemplate;
	}

	/**
	 * Gets the cC list template of the mail.
	 *
	 * @return the cC list template of the mail
	 */
	public String getCcListTemplate()
	{
		return ccListTemplate;
	}

	/**
	 * Sets the cC list template of the mail.
	 *
	 * @param ccListTemplate the new cC list template of the mail
	 */
	public void setCcListTemplate(String ccListTemplate)
	{
		this.ccListTemplate = ccListTemplate;
	}

	/**
	 * Gets the bCC list template of the mail.
	 *
	 * @return the bCC list template of the mail
	 */
	public String getBccListTemplate()
	{
		return bccListTemplate;
	}

	/**
	 * Sets the bCC list template of the mail.
	 *
	 * @param bccListTemplate the new bCC list template of the mail
	 */
	public void setBccListTemplate(String bccListTemplate)
	{
		this.bccListTemplate = bccListTemplate;
	}

	/**
	 * Gets the subject template of the mail.
	 *
	 * @return the subject template of the mail
	 */
	public String getSubjectTemplate()
	{
		return subjectTemplate;
	}

	/**
	 * Sets the subject template of the mail.
	 *
	 * @param subjectTemplate the new subject template of the mail
	 */
	public void setSubjectTemplate(String subjectTemplate)
	{
		this.subjectTemplate = subjectTemplate;
	}

	/**
	 * Gets the content template of the mail.
	 *
	 * @return the content template of the mail
	 */
	public String getContentTemplate()
	{
		return contentTemplate;
	}

	/**
	 * Sets the content template of the mail.
	 *
	 * @param contentTemplate the new content template of the mail
	 */
	public void setContentTemplate(String contentTemplate)
	{
		this.contentTemplate = contentTemplate;
	}

	/**
	 * Gets the customization object that can be use by application to set customization parameters that will be used for template processing.
	 *
	 * @return the customization object that can be use by application to set customization parameters that will be used for template processing
	 */
	public Object getCustomization()
	{
		return customization;
	}

	/**
	 * Sets the customization object that can be use by application to set customization parameters that will be used for template processing.
	 *
	 * @param customization the new customization object that can be use by application to set customization parameters that will be used for template processing
	 */
	public void setCustomization(Object customization)
	{
		this.customization = customization;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Subject: ").append(subjectTemplate);

		if(toListTemplate != null)
		{
			builder.append(",").append("To: ").append(toListTemplate);
		}

		if(ccListTemplate != null)
		{
			builder.append(",").append("CC: ").append(ccListTemplate);
		}

		if(bccListTemplate != null)
		{
			builder.append(",").append("BCC: ").append(bccListTemplate);
		}

		builder.append("]");
		return builder.toString();
	}
}
