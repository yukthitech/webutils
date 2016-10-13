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

package com.yukthi.webutils.mail;

import java.util.Arrays;
import java.util.Collection;

/**
 * Represents data of the mail to be sent.
 * 
 * @author akiran
 */
public class EmailData
{
	/**
	 * Name of template used for building this data object.
	 */
	private String templateName;
	
	/**
	 * To list of the mail.
	 */
	private String toList[];

	/**
	 * CC list of the mail.
	 */
	private String ccList[];

	/**
	 * BCC list of the mail.
	 */
	private String bccList[];

	/**
	 * Subject template of the mail.
	 */
	private String subjectTemplate;

	/**
	 * Content template of the mail.
	 */
	private String contentTemplate;

	/**
	 * Attachments to be sent.
	 */
	private Collection<FileAttachment> attachments;

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
	 * Gets the to list of the mail.
	 *
	 * @return the to list of the mail
	 */
	public String[] getToList()
	{
		return toList;
	}

	/**
	 * Sets the to list of the mail.
	 *
	 * @param toList the new to list of the mail
	 */
	public void setToList(String[] toList)
	{
		this.toList = toList;
	}

	/**
	 * Gets the cC list of the mail.
	 *
	 * @return the cC list of the mail
	 */
	public String[] getCcList()
	{
		return ccList;
	}

	/**
	 * Sets the cC list of the mail.
	 *
	 * @param ccList the new cC list of the mail
	 */
	public void setCcList(String[] ccList)
	{
		this.ccList = ccList;
	}

	/**
	 * Gets the bCC list of the mail.
	 *
	 * @return the bCC list of the mail
	 */
	public String[] getBccList()
	{
		return bccList;
	}

	/**
	 * Sets the bCC list of the mail.
	 *
	 * @param bccList the new bCC list of the mail
	 */
	public void setBccList(String[] bccList)
	{
		this.bccList = bccList;
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
	 * Gets the attachments to be sent.
	 *
	 * @return the attachments to be sent
	 */
	public Collection<FileAttachment> getAttachments()
	{
		return attachments;
	}

	/**
	 * Sets the attachments to be sent.
	 *
	 * @param attachments the new attachments to be sent
	 */
	public void setAttachments(Collection<FileAttachment> attachments)
	{
		this.attachments = attachments;
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

		if(toList != null)
		{
			builder.append(",").append("To: ").append(Arrays.toString(toList));
		}

		if(ccList != null)
		{
			builder.append(",").append("CC: ").append(Arrays.toString(ccList));
		}

		if(bccList != null)
		{
			builder.append(",").append("BCC: ").append(Arrays.toString(bccList));
		}

		builder.append("]");
		return builder.toString();
	}
}
