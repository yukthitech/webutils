package com.yukthitech.webutils.autox;

import com.yukthitech.webutils.common.controllers.ICrudController;
import com.yukthitech.webutils.common.models.BaseResponse;
import com.yukthitech.webutils.common.models.BasicSaveResponse;

/**
 * Abstraction for autox reports controller.
 * @author akiran
 * @param <R> Http multi part request
 */
public interface IAutoxReportController<R> extends ICrudController<AutoxReportModel, IAutoxReportController<R>>
{
	/**
	 * Used to upload autox report with details.
	 * @param autoxReport report to upload
	 * @param request the request
	 * @return success or failure response.
	 */
	public BasicSaveResponse uploadReport(AutoxReportModel autoxReport, R request);
	
	/**
	 * Bulk deletes the specified reports.
	 *
	 * @param request List of reports to delete
	 * @return success or failure response.
	 */
	public BaseResponse bulkDelete(BulkReportDeleteRequest request);
}
