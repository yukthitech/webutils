package com.yukthitech.webutils.mail;

import java.util.List;

/**
 * Represents mail message being sent by the application.
 * @author akiran
 */
public class MailMessage
{
	/**
	 * To list.
	 */
	private List<String> toList;
	
	/**
	 * CC List.
	 */
	private List<String> ccList;
	
	/**
	 * BCC List.
	 */
	private List<String> bccList;
	
	/**
	 * Subject of the mail being sent.
	 */
	private String subject;
	
	/**
	 * Body of the mail being sent.
	 */
	private String body;
	
	/**
	 * In cases the mail being sent is reply to received mail, this field will be populated
	 * with received mail object.
	 */
	private ReceivedMailMessage receivedMailMessage;
	
	/**
	 * Instantiates a new mail message.
	 */
	public MailMessage()
	{}

	/**
	 * Gets the to list.
	 *
	 * @return the to list
	 */
	public List<String> getToList()
	{
		return toList;
	}

	/**
	 * Sets the to list.
	 *
	 * @param toList the new to list
	 */
	public void setToList(List<String> toList)
	{
		this.toList = toList;
	}

	/**
	 * Gets the cC List.
	 *
	 * @return the cC List
	 */
	public List<String> getCcList()
	{
		return ccList;
	}

	/**
	 * Sets the cC List.
	 *
	 * @param ccList the new cC List
	 */
	public void setCcList(List<String> ccList)
	{
		this.ccList = ccList;
	}

	/**
	 * Gets the bCC List.
	 *
	 * @return the bCC List
	 */
	public List<String> getBccList()
	{
		return bccList;
	}

	/**
	 * Sets the bCC List.
	 *
	 * @param bccList the new bCC List
	 */
	public void setBccList(List<String> bccList)
	{
		this.bccList = bccList;
	}

	/**
	 * Gets the subject of the mail being sent.
	 *
	 * @return the subject of the mail being sent
	 */
	public String getSubject()
	{
		return subject;
	}

	/**
	 * Sets the subject of the mail being sent.
	 *
	 * @param subject the new subject of the mail being sent
	 */
	public void setSubject(String subject)
	{
		this.subject = subject;
	}

	/**
	 * Gets the body of the mail being sent.
	 *
	 * @return the body of the mail being sent
	 */
	public String getBody()
	{
		return body;
	}

	/**
	 * Sets the body of the mail being sent.
	 *
	 * @param body the new body of the mail being sent
	 */
	public void setBody(String body)
	{
		this.body = body;
	}

	/**
	 * Gets the in cases the mail being sent is reply to received mail, this field will be populated with received mail object.
	 *
	 * @return the in cases the mail being sent is reply to received mail, this field will be populated with received mail object
	 */
	public ReceivedMailMessage getReceivedMailMessage()
	{
		return receivedMailMessage;
	}

	/**
	 * Sets the in cases the mail being sent is reply to received mail, this field will be populated with received mail object.
	 *
	 * @param receivedMailMessage the new in cases the mail being sent is reply to received mail, this field will be populated with received mail object
	 */
	public void setReceivedMailMessage(ReceivedMailMessage receivedMailMessage)
	{
		this.receivedMailMessage = receivedMailMessage;
	}
}
