package com.yukthitech.webutils.autox;

import com.yukthitech.persistence.repository.annotations.Field;
import com.yukthitech.webutils.common.BasicSearchResult;
import com.yukthitech.webutils.common.annotations.Model;

/**
 * Represents search result of the employer.
 * @author akiran
 */
@Model
public class AutoxReportSearchResult extends BasicSearchResult
{
	/**
	 * Name of the employer.
	 */
	@Field("name")
	private String name;
	
	/**
	 * source of report.
	 */
	@Field("source")
	private String source;
	
	/**
	 * Description about report.
	 */
	@Field("description")
	private String description;
	
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
	 * Gets the source of report.
	 *
	 * @return the source of report
	 */
	public String getSource()
	{
		return source;
	}

	/**
	 * Sets the source of report.
	 *
	 * @param source the new source of report
	 */
	public void setSource(String source)
	{
		this.source = source;
	}

	/**
	 * Gets the description about report.
	 *
	 * @return the description about report
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Sets the description about report.
	 *
	 * @param description the new description about report
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}
}
