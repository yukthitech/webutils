package com.webutils.services.mail;

import java.util.List;

import lombok.Data;

/**
 * Represents mail message being sent by the application.
 * @author akiran
 */
@Data
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
}
