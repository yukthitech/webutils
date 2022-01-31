package com.yukthitech.webutils.autox;

import javax.persistence.Column;
import javax.persistence.Table;

import com.yukthitech.persistence.annotations.Indexed;
import com.yukthitech.webutils.repository.WebutilsTrackedEntity;

/**
 * Autox report entity.
 * @author akiran
 */
@Table(name = "WEBUTILS_AUTOX_REPORT")
public class AutoxReportEntity extends WebutilsTrackedEntity
{
	/**
	 * Named of the report.
	 */
	@Column(name = "NAME", length = 100, nullable = false)
	@Indexed
	private String name;
	
	/**
	 * Source which is uploading the report.
	 */
	@Column(name = "SOURCE", length = 100, nullable = false)
	private String source;
	
	/**
	 * Description about the report.
	 */
	@Column(name = "DESCRIPTION", length = 500, nullable = true)
	private String description;

	/**
	 * Gets the named of the report.
	 *
	 * @return the named of the report
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the named of the report.
	 *
	 * @param name the new named of the report
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the source which is uploading the report.
	 *
	 * @return the source which is uploading the report
	 */
	public String getSource()
	{
		return source;
	}

	/**
	 * Sets the source which is uploading the report.
	 *
	 * @param source the new source which is uploading the report
	 */
	public void setSource(String source)
	{
		this.source = source;
	}

	/**
	 * Gets the description about the report.
	 *
	 * @return the description about the report
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Sets the description about the report.
	 *
	 * @param description the new description about the report
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}
}
