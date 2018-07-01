package com.yukthitech.webutils.autox;

import java.util.List;

import com.yukthitech.validation.annotations.NotEmpty;
import com.yukthitech.validation.annotations.Required;
import com.yukthitech.webutils.common.annotations.Model;

/**
 * Request to update multiple schedules in one shot.
 * 
 * @author akiran
 */
@Model
public class BulkReportDeleteRequest
{
	/**
	 * List of report ids to delete.
	 */
	@Required
	@NotEmpty
	private List<Long> reportIds;

	/**
	 * Gets the list of report ids to delete.
	 *
	 * @return the list of report ids to delete
	 */
	public List<Long> getReportIds()
	{
		return reportIds;
	}

	/**
	 * Sets the list of report ids to delete.
	 *
	 * @param reportIds the new list of report ids to delete
	 */
	public void setReportIds(List<Long> reportIds)
	{
		this.reportIds = reportIds;
	}
}
