package com.yukthitech.webutils.autox;

import java.util.Date;

import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.persistence.repository.annotations.Operator;
import com.yukthitech.webutils.common.annotations.Label;
import com.yukthitech.webutils.common.annotations.Model;

/**
 * Candidate search query.
 * @author akiran
 */
@Model
public class AutoxReportSearchQuery
{
	/**
	 * Name of the employer.
	 */
	@Condition(value = "name", op = Operator.LIKE)
	private String name;
	
	/**
	 * POC name of the employer.
	 */
	@Label("Created After")
	@Condition(value = "createdOn", op = Operator.GE)
	private Date createdAfter;
	
	/**
	 * Phone number of the employer.
	 */
	@Label("Created Before")
	@Condition(value = "createdOn", op = Operator.LE)
	private Date createdBefore;

	/**
	 * Gets the name of the employer.
	 *
	 * @return the name of the employer
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the employer.
	 *
	 * @param name the new name of the employer
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the pOC name of the employer.
	 *
	 * @return the pOC name of the employer
	 */
	public Date getCreatedAfter()
	{
		return createdAfter;
	}

	/**
	 * Sets the pOC name of the employer.
	 *
	 * @param createdAfter the new pOC name of the employer
	 */
	public void setCreatedAfter(Date createdAfter)
	{
		this.createdAfter = createdAfter;
	}

	/**
	 * Gets the phone number of the employer.
	 *
	 * @return the phone number of the employer
	 */
	public Date getCreatedBefore()
	{
		return createdBefore;
	}

	/**
	 * Sets the phone number of the employer.
	 *
	 * @param createdBefore the new phone number of the employer
	 */
	public void setCreatedBefore(Date createdBefore)
	{
		this.createdBefore = createdBefore;
	}
}
