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

/**
 * Represents data of the mail to be sent
 * @author akiran
 */
public class EmailData
{
	/**
	 * To list of the mail
	 */
	private String toList[];
	
	/**
	 * CC list of the mail
	 */
	private String ccList[];
	
	/**
	 * BCC list of the mail
	 */
	private String bccList[];
	
	/**
	 * From address of the mail
	 */
	private String fromId;
	
	/**
	 * Subject of the mail
	 */
	private String subject;
	
	/**
	 * Content of the mail
	 */
	private String content;
	
	/**
	 * Attachments to be sent
	 */
	private FileAttachment attachments[];

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
	 * Gets the from address of the mail.
	 *
	 * @return the from address of the mail
	 */
	public String getFromId()
	{
		return fromId;
	}

	/**
	 * Sets the from address of the mail.
	 *
	 * @param fromId the new from address of the mail
	 */
	public void setFromId(String fromId)
	{
		this.fromId = fromId;
	}

	/**
	 * Gets the subject of the mail.
	 *
	 * @return the subject of the mail
	 */
	public String getSubject()
	{
		return subject;
	}

	/**
	 * Sets the subject of the mail.
	 *
	 * @param subject the new subject of the mail
	 */
	public void setSubject(String subject)
	{
		this.subject = subject;
	}

	/**
	 * Gets the content of the mail.
	 *
	 * @return the content of the mail
	 */
	public String getContent()
	{
		return content;
	}

	/**
	 * Sets the content of the mail.
	 *
	 * @param content the new content of the mail
	 */
	public void setContent(String content)
	{
		this.content = content;
	}

	/**
	 * Gets the attachments to be sent.
	 *
	 * @return the attachments to be sent
	 */
	public FileAttachment[] getAttachments()
	{
		return attachments;
	}

	/**
	 * Sets the attachments to be sent.
	 *
	 * @param attachments the new attachments to be sent
	 */
	public void setAttachments(FileAttachment... attachments)
	{
		this.attachments = attachments;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("From: ").append(fromId);
		builder.append(",").append("Subject: ").append(subject);
		
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

