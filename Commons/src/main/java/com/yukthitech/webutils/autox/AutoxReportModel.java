package com.yukthitech.webutils.autox;

import com.yukthitech.validation.annotations.MaxLen;
import com.yukthitech.validation.annotations.Required;
import com.yukthitech.webutils.common.BaseModel;
import com.yukthitech.webutils.common.FileInfo;
import com.yukthitech.webutils.common.annotations.Model;

/**
 * Autox report details.
 * @author akiran
 */
@Model
public class AutoxReportModel extends BaseModel
{
	/**
	 * Named of the report.
	 */
	@Required
	@MaxLen(100)
	private String name;
	
	/**
	 * Source which is uploading the report.
	 */
	@Required
	@MaxLen(100)
	private String source;
	
	/**
	 * Description about the report.
	 */
	@MaxLen(500)
	private String description;
	
	/**
	 * Report zip file.
	 */
	private FileInfo reportFile;

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

	/**
	 * Gets the report zip file.
	 *
	 * @return the report zip file
	 */
	public FileInfo getReportFile()
	{
		return reportFile;
	}

	/**
	 * Sets the report zip file.
	 *
	 * @param reportFile the new report zip file
	 */
	public void setReportFile(FileInfo reportFile)
	{
		this.reportFile = reportFile;
	}
}
