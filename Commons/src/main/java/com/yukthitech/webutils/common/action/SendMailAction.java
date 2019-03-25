package com.yukthitech.webutils.common.action;

/**
 * Action for sending mail.
 */
public class SendMailAction extends AbstractAgentAction
{
	/**
	 * Role of the employee to whom the mail has to be sent.
	 */
	private String toEmployeeRole;
	
	/**
	 * Subject of the mail being sent.
	 */
	private String subject;
	
	/**
	 * Body of the mail being sent.
	 */
	private String body;

	/**
	 * Gets the role of the employee to whom the mail has to be sent.
	 *
	 * @return the role of the employee to whom the mail has to be sent
	 */
	public String getToEmployeeRole()
	{
		return toEmployeeRole;
	}

	/**
	 * Sets the role of the employee to whom the mail has to be sent.
	 *
	 * @param toEmployeeRole the new role of the employee to whom the mail has to be sent
	 */
	public void setToEmployeeRole(String toEmployeeRole)
	{
		this.toEmployeeRole = toEmployeeRole;
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
} 