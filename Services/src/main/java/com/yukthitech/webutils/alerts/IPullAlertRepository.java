package com.yukthitech.webutils.alerts;

import java.util.List;

import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.persistence.repository.annotations.DefaultCondition;
import com.yukthitech.persistence.repository.annotations.Field;
import com.yukthitech.persistence.repository.annotations.MethodConditions;
import com.yukthitech.persistence.repository.annotations.OrderBy;
import com.yukthitech.webutils.common.alerts.AlertProcessedDetails;
import com.yukthitech.webutils.common.alerts.PullAlertStatus;
import com.yukthitech.webutils.repository.IWebutilsRepository;

/**
 * Repository for pull alerts.
 * @author akiran
 */
public interface IPullAlertRepository extends IWebutilsRepository<PullAlertEntity>
{
	/**
	 * Fetches alerts for specified source. 
	 * @param target target for which alerts to be fetched
	 * @return matching alerts.
	 */
	@OrderBy("id")
	@MethodConditions(conditions = {
		@DefaultCondition(field = "status", value = "NOT_PROCESSED")
		})
	public List<PullAlertEntity> fetchAlerts(@Condition("target") String target);
	
	/**
	 * Updates the status of specified alert.
	 * @param id id of alert to update
	 * @param status status to update
	 * @param alertProcessedDetails Alert process details.
	 */
	public void updateStatus(@Condition("id") long id, @Field("status") PullAlertStatus status, @Field("alertProcessedDetails") AlertProcessedDetails alertProcessedDetails);
}
