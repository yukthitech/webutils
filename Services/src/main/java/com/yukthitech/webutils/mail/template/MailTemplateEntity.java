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

package com.yukthitech.webutils.mail.template;

import javax.persistence.Column;
import javax.persistence.Table;

import com.yukthitech.persistence.annotations.DataType;
import com.yukthitech.persistence.annotations.DataTypeMapping;
import com.yukthitech.persistence.annotations.UniqueConstraint;
import com.yukthitech.persistence.annotations.UniqueConstraints;
import com.yukthitech.persistence.conversion.impl.JsonWithTypeConverter;
import com.yukthitech.webutils.repository.WebutilsBaseEntity;

/**
 * Represents data of the mail to be sent.
 * 
 * @author akiran
 */
@UniqueConstraints({
	@UniqueConstraint(name = "TEMPLATE_NAME", fields = {"templateName", "ownerEntityType", "ownerEntityId"}, finalName = false)
	})
@Table(name = "WEBUTILS_MAIL_TEMPLATE")
public class MailTemplateEntity extends WebutilsBaseEntity
{
	/**
	 * Name of template used for building this data object.
	 */
	@Column(name = "NAME", length = 100, nullable = false)
	private String templateName;

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

	/**
	 * Owner entity type.
	 */
	@Column(name = "OWNER_ENTITY_TYPE", length = 250, nullable = false)
	private String ownerEntityType;

	/**
	 * Owner entity id.
	 */
	@Column(name = "OWNER_ENTITY_ID", nullable = false)
	private Long ownerEntityId = 0L;
	
	/**
	 * Instantiates a new mail template entity.
	 */
	public MailTemplateEntity()
	{
	}
	
	/**
	 * Instantiates a new mail template entity.
	 *
	 * @param toListTemplate the to list template
	 * @param ccListTemplate the cc list template
	 * @param subjectTemplate the subject template
	 * @param contentTemplate the content template
	 */
	public MailTemplateEntity(String toListTemplate, String ccListTemplate, String subjectTemplate, String contentTemplate)
	{
		this.toListTemplate = toListTemplate;
		this.ccListTemplate = ccListTemplate;
		this.subjectTemplate = subjectTemplate;
		this.contentTemplate = contentTemplate;
	}

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

	/**
	 * Gets the owner entity type.
	 *
	 * @return the owner entity type
	 */
	public String getOwnerEntityType()
	{
		return ownerEntityType;
	}

	/**
	 * Sets the owner entity type.
	 *
	 * @param ownerEntityType the new owner entity type
	 */
	public void setOwnerEntityType(String ownerEntityType)
	{
		this.ownerEntityType = ownerEntityType;
	}

	/**
	 * Gets the owner entity id.
	 *
	 * @return the owner entity id
	 */
	public Long getOwnerEntityId()
	{
		return ownerEntityId;
	}

	/**
	 * Sets the owner entity id.
	 *
	 * @param ownerEntityId the new owner entity id
	 */
	public void setOwnerEntityId(Long ownerEntityId)
	{
		this.ownerEntityId = ownerEntityId;
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
